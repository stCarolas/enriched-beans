package com.github.stcarolas.enrichedbeans.javamodel.variable;

import javax.inject.Inject;
import javax.inject.Named;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import com.github.stcarolas.enrichedbeans.javamodel.annotation.AbstractAnnotationFactory;
import com.squareup.javapoet.ParameterizedTypeName;
import io.vavr.collection.List;
import io.vavr.collection.Seq;
import static io.vavr.API.*;

@Named
public class AbstractVariableFactory {
  private AbstractAnnotationFactory annotationFactory;
  private Seq<VariableFactory> variableFactories;

  @Inject
  public AbstractVariableFactory(
    AbstractAnnotationFactory annotationFactory,
    @Named("VariableFactories") Seq<VariableFactory> variableFactories
  ) {
    this.annotationFactory = annotationFactory;
    this.variableFactories = variableFactories;
  }

  public Variable from(Element element) {
    return variableFactories.flatMap(factory -> factory.from(element))
      .headOption()
      .getOrElse(defaultImplementation(element));
  }

  protected final Variable defaultImplementation(Element element) {
    return ImmutableVariableImpl.builder()
      .name(element.getSimpleName().toString())
      .type(ParameterizedTypeName.get(element.asType()))
      .annotations(
        List.ofAll(element.getAnnotationMirrors()).map(annotationFactory::from)
      )
      .modifiers(List(Modifier.PRIVATE))
      .build();
  }
}
