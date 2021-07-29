package com.github.stcarolas.enrichedbeans.processor.java.factories;

import javax.inject.Inject;
import javax.inject.Named;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import com.github.stcarolas.enrichedbeans.processor.java.ImmutableMethod;
import com.github.stcarolas.enrichedbeans.processor.java.Method;
import com.github.stcarolas.enrichedbeans.processor.java.annotation.AnnotationFactory;
import com.squareup.javapoet.TypeName;
import io.vavr.collection.List;

@Named
public class MethodFactory {
  private VariableFactory variableFactory;
  private AnnotationFactory annotationFactory;

  @Inject
  public MethodFactory(
    VariableFactory variableFactory,
    AnnotationFactory annotationFactory
  ) {
    this.annotationFactory = annotationFactory;
    this.variableFactory = variableFactory;
  }

  public Method from(Element element) {
    ExecutableElement originalMethod = (ExecutableElement) element;
    return ImmutableMethod.builder()
      .name(originalMethod.getSimpleName().toString())
      .returnType(TypeName.get(originalMethod.getReturnType()))
      .parameters(List.ofAll(originalMethod.getParameters()).map(variableFactory::from))
      .annotations(
        List.ofAll(originalMethod.getAnnotationMirrors()).map(annotationFactory::from)
      )
      .build();
  }
}
