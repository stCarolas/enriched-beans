package com.github.stcarolas.enrichedbeans.javamodel.method;

import javax.inject.Inject;
import javax.inject.Named;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;

import com.github.stcarolas.enrichedbeans.javamodel.annotation.AbstractAnnotationFactory;
import com.github.stcarolas.enrichedbeans.javamodel.variable.AbstractVariableFactory;
import com.squareup.javapoet.TypeName;

import io.vavr.collection.List;
import io.vavr.collection.Seq;

@Named
public class AbstractMethodFactory {
  private AbstractVariableFactory variableFactory;
  private AbstractAnnotationFactory annotationFactory;
  private Seq<MethodFactory> factories;

  @Inject
  public AbstractMethodFactory(
    AbstractVariableFactory variableFactory,
    AbstractAnnotationFactory annotationFactory,
    @Named("MethodFactories") Seq<MethodFactory> factories
  ) {
    this.annotationFactory = annotationFactory;
    this.variableFactory = variableFactory;
    this.factories = factories;
  }

  public Method from(Element element){
    return factories.flatMap(f -> f.from(element))
      .headOption()
      .getOrElse(defaultImplementation(element));
  }

  protected final Method defaultImplementation(Element element) {
    ExecutableElement originalMethod = (ExecutableElement) element;
    return ImmutableMethodImpl.builder()
      .name(originalMethod.getSimpleName().toString())
      .returnType(TypeName.get(originalMethod.getReturnType()))
      .parameters(List.ofAll(originalMethod.getParameters()).map(variableFactory::from))
      .annotations(
        List.ofAll(originalMethod.getAnnotationMirrors()).flatMap(annotationFactory::from)
      )
      .build();
  }
}
