package  com.github.stcarolas.enrichedbeans.processor.java.factories;

import javax.inject.Inject;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;

import com.github.stcarolas.enrichedbeans.processor.java.ImmutableMethod;
import com.github.stcarolas.enrichedbeans.processor.java.Method;
import com.squareup.javapoet.TypeName;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.vavr.collection.List;

public class MethodFactory {

  public Method from(Element element){
    ExecutableElement originalMethod = (ExecutableElement)element;
    return ImmutableMethod.builder()
      .name(originalMethod.getSimpleName().toString())
      .returnType(TypeName.get(originalMethod.getReturnType()))
      .parameters(List.ofAll(originalMethod.getParameters()).map(variableFactory::from))
      .annotations(List.ofAll(originalMethod.getAnnotationMirrors()).map(annotationFactory::from))
      .build();
  }

  @Inject public MethodFactory(){};

  private VariableFactory variableFactory = new VariableFactory();
  private AnnotationFactory annotationFactory = new AnnotationFactory();

  static final Logger log = LogManager.getLogger();
}
