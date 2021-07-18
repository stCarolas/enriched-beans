package com.github.stcarolas.enrichedbeans.processor.java.factories;

import javax.inject.Inject;
import javax.inject.Named;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;
import com.github.stcarolas.enrichedbeans.processor.java.Variable;
import com.github.stcarolas.enrichedbeans.processor.java.ImmutableVariable;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import io.vavr.collection.List;
import static io.vavr.API.*;

@Named
public class VariableFactory {
  private AnnotationFactory annotationFactory;

  @Inject
  public VariableFactory(AnnotationFactory annotationFactory) {
    this.annotationFactory = annotationFactory;
  }

  public Variable from(Element element) {
    return from((VariableElement) element);
  }

  public Variable from(String name, TypeName type) {
    return ImmutableVariable.builder().name(name).type(type).build();
  }

  public Variable from(VariableElement element) {
    return ImmutableVariable.builder()
      .name(element.getSimpleName().toString())
      .type(ParameterizedTypeName.get(element.asType()))
      .annotations(
        List.ofAll(element.getAnnotationMirrors()).map(annotationFactory::from)
      )
      .modifiers(List(Modifier.PRIVATE))
      .build();
  }
}
