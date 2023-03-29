package io.github.stcarolas.enrichedbeans.javamodel.method;

import javax.inject.Inject;
import javax.inject.Named;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;

import io.github.stcarolas.enrichedbeans.javamodel.annotation.AbstractAnnotationFactory;
import io.github.stcarolas.enrichedbeans.javamodel.variable.AbstractVariableFactory;

import com.squareup.javapoet.CodeBlock;
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
    TypeName returnType = TypeName.get(originalMethod.getReturnType());
    return ImmutableMethodImpl.builder()
      .name(originalMethod.getSimpleName().toString())
      .returnType(returnType)
      .parameters(List.ofAll(originalMethod.getParameters()).map(variableFactory::from))
      .annotations(
        List.ofAll(originalMethod.getAnnotationMirrors()).flatMap(annotationFactory::from)
      )
      .code(TypeName.VOID.equals(returnType) ? CodeBlock.of("return;") : CodeBlock.of("return null;"))
      .build();
  }
}
