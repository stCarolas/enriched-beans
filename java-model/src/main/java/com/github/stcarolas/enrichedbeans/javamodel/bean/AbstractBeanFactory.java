package  com.github.stcarolas.enrichedbeans.javamodel.bean;

import javax.inject.Inject;
import javax.inject.Named;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;

import com.github.stcarolas.enrichedbeans.javamodel.annotation.AbstractAnnotationFactory;
import com.github.stcarolas.enrichedbeans.javamodel.annotation.Annotation;
import com.github.stcarolas.enrichedbeans.javamodel.method.AbstractMethodFactory;
import com.github.stcarolas.enrichedbeans.javamodel.method.Method;
import com.github.stcarolas.enrichedbeans.javamodel.variable.AbstractVariableFactory;
import com.github.stcarolas.enrichedbeans.javamodel.variable.Variable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.vavr.collection.List;
import io.vavr.collection.Seq;

@Named("AbstractBeanFactory")
public class AbstractBeanFactory {
  private static final Logger log = LogManager.getLogger();

  private AbstractVariableFactory variableFactory;
  private AbstractAnnotationFactory annotationFactory;
  private AbstractMethodFactory methodFactory;
  private Seq<BeanFactory> factories;

  @Inject
  public AbstractBeanFactory(
      AbstractVariableFactory variableFactory,
      AbstractAnnotationFactory annotationFactory,
      AbstractMethodFactory methodFactory,
      @Named("BeanFactories") Seq<BeanFactory> factories
  ){
    this.variableFactory = variableFactory;
    this.annotationFactory = annotationFactory;
    this.methodFactory = methodFactory;
    this.factories = factories;
  }

  public Bean from(Element origin){
    return factories.flatMap(factory -> factory.from(origin))
      .headOption()
      .getOrElse(defaultImplementation(origin));
  }

  protected final Bean defaultImplementation(Element origin) {
    return ImmutableBeanImpl.builder()
      .className(origin.getSimpleName().toString())
      .packageName(packageName(origin))
      .fields(fields(origin))
      .constructors(constructors(origin))
      .annotations(annotations(origin))
      .methods(methods(origin))
      .build();
  }

  private String packageName(Element original) {
    String fullName = ((TypeElement) original).getQualifiedName().toString();
    String packageName = "";
    int lastDot = fullName.lastIndexOf('.');
    if (lastDot > 0) {
      packageName = fullName.substring(0, lastDot);
    }
    return packageName;
  }

  private List<Variable> fields(Element origin) {
    return List.ofAll(origin.getEnclosedElements())
      .filter(element -> element.getKind().isField())
      .map(variableFactory::from);
  }

  private Seq<ExecutableElement> constructors(Element origin) {
    return List.ofAll(origin.getEnclosedElements())
      .filter(element -> element.getKind().equals(ElementKind.CONSTRUCTOR))
      .map($ -> (ExecutableElement) $);
  }

  private Seq<Annotation> annotations(Element original) {
    return List.ofAll(original.getAnnotationMirrors()).map(annotationFactory::from);
  }

  private Seq<Method> methods(Element original) {
    return List.ofAll(original.getEnclosedElements())
      .filter(element -> element.getKind().equals(ElementKind.METHOD))
      .map(methodFactory::from);
  }

}
