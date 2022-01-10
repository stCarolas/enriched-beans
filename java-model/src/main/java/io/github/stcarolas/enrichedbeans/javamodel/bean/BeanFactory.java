package com.github.stcarolas.enrichedbeans.javamodel.bean;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

import com.github.stcarolas.enrichedbeans.javamodel.Environment;
import com.github.stcarolas.enrichedbeans.javamodel.annotation.AbstractAnnotationFactory;
import com.github.stcarolas.enrichedbeans.javamodel.annotation.Annotation;
import com.github.stcarolas.enrichedbeans.javamodel.method.AbstractMethodFactory;
import com.github.stcarolas.enrichedbeans.javamodel.method.Method;
import com.github.stcarolas.enrichedbeans.javamodel.variable.AbstractVariableFactory;
import com.github.stcarolas.enrichedbeans.javamodel.variable.Variable;
import com.squareup.javapoet.TypeName;

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

  abstract public Option<Bean> from(Element origin);

  protected final Bean defaultImplementation(Element origin) {
    return ImmutableEnrichableBeanImpl.builder()
      .className(origin.getSimpleName().toString())
      .packageName(packageName(origin))
      .fields(fields(origin))
      .type(TypeName.get(origin.asType()))
      .constructors(constructors(origin))
      .annotations(annotations(origin))
      .methods(methods(origin))
      .env(env)
      .build();
  }

  protected final String packageName(Element original) {
    String fullName = ((TypeElement) original).getQualifiedName().toString();
    String packageName = "";
    int lastDot = fullName.lastIndexOf('.');
    if (lastDot > 0) {
      packageName = fullName.substring(0, lastDot);
    }
    return packageName;
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
    return List.ofAll(original.getAnnotationMirrors()).flatMap(annotationFactory::from);
  }

  private Seq<Method> methods(Element original) {
    return List.ofAll(original.getEnclosedElements())
      .filter(element -> element.getKind().equals(ElementKind.METHOD))
      .map(methodFactory::from);
  }
}
