package io.github.stcarolas.enrichedbeans.javamodel.bean;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

import io.github.stcarolas.enrichedbeans.javamodel.Environment;
import io.github.stcarolas.enrichedbeans.javamodel.annotation.AbstractAnnotationFactory;
import io.github.stcarolas.enrichedbeans.javamodel.annotation.Annotation;
import io.github.stcarolas.enrichedbeans.javamodel.method.AbstractMethodFactory;
import io.github.stcarolas.enrichedbeans.javamodel.method.Method;
import io.github.stcarolas.enrichedbeans.javamodel.variable.AbstractVariableFactory;
import io.github.stcarolas.enrichedbeans.javamodel.variable.Variable;

import io.vavr.collection.List;
import io.vavr.collection.Seq;
import io.vavr.control.Option;

public abstract class BeanFactory {

  private final AbstractVariableFactory variableFactory;
  private final AbstractAnnotationFactory annotationFactory;
  private final AbstractMethodFactory methodFactory;
  private final Environment env;

  public BeanFactory(
    AbstractVariableFactory variableFactory,
    AbstractAnnotationFactory annotationFactory,
    AbstractMethodFactory methodFactory,
    Environment env
  ){
    this.variableFactory = variableFactory;
    this.annotationFactory = annotationFactory;
    this.methodFactory = methodFactory;
    this.env = env;
  }

  abstract public Option<? extends Bean> from(Element origin);

  protected final ProcessedBean defaultImplementation(Element origin) {
    TypeElement type = ((TypeElement) origin);
    boolean isAbstract = type.getModifiers().contains(Modifier.ABSTRACT);
    return ImmutableProcessedBean.builder()
      .type(type)
      .fields(fields(origin))
      .isAbstract(isAbstract)
      .constructors(constructors(origin))
      .annotations(annotations(origin))
      .methods(methods(origin))
      .env(env)
      .build();
  }


  protected final List<Variable> fields(Element origin) {
    return List.ofAll(origin.getEnclosedElements())
      .filter(element -> element.getKind().isField())
      .map(variableFactory::from);
  }

  protected final Seq<ExecutableElement> constructors(Element origin) {
    return List.ofAll(origin.getEnclosedElements())
      .filter(element -> element.getKind().equals(ElementKind.CONSTRUCTOR))
      .map($ -> (ExecutableElement) $);
  }

  protected final Seq<Annotation> annotations(Element original) {
    return List.ofAll(original.getAnnotationMirrors())
      .flatMap(annotationFactory::from);
  }

  private Seq<Method> methods(Element original) {
    return List.ofAll(original.getEnclosedElements())
      .filter(element -> element.getKind().equals(ElementKind.METHOD))
      .map(methodFactory::from);
  }

}
