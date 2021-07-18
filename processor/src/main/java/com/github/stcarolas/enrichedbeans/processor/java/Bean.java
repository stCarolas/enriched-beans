package com.github.stcarolas.enrichedbeans.processor.java;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

import com.github.stcarolas.enrichedbeans.processor.java.factories.AnnotationFactory;
import com.github.stcarolas.enrichedbeans.processor.java.factories.MethodFactory;
import com.github.stcarolas.enrichedbeans.processor.java.factories.VariableFactory;
import com.squareup.javapoet.TypeName;

import org.immutables.value.Value.Immutable;

import io.vavr.collection.List;
import io.vavr.collection.Seq;
import io.vavr.control.Option;
import static io.vavr.API.*;

@Immutable
public abstract class Bean {
  abstract Element original();
  abstract VariableFactory fieldFactory();
  abstract AnnotationFactory annotationFactory();
  abstract MethodFactory methodFactory();

  public TypeName type() {
    return TypeName.get(original().asType());
  }

  public String name() {
    return original().getSimpleName().toString();
  }

  public Seq<TypeElement> subtypes() {
    return List.ofAll(original().getEnclosedElements())
      .filter(element -> element.getKind().isInterface())
      .map(element -> (TypeElement) element);
  }

  public Seq<Variable> fields() {
    return List.ofAll(original().getEnclosedElements())
      .filter(element -> element.getKind().isField())
      .map(fieldFactory()::from);
  }

  public Seq<ExecutableElement> constructors() {
    return List.ofAll(original().getEnclosedElements())
      .filter(element -> element.getKind().equals(ElementKind.CONSTRUCTOR))
      .map($ -> (ExecutableElement) $);
  }

  public Seq<Annotation> annotations() {
    return List.ofAll(original().getAnnotationMirrors()).map(annotationFactory()::from);
  }

  public Seq<Method> methods() {
    return List.ofAll(original().getEnclosedElements())
      .filter(element -> element.getKind().equals(ElementKind.METHOD))
      .map(methodFactory()::from);
  }

  public String packageName() {
    String fullName = ((TypeElement) original()).getQualifiedName().toString();
    String packageName = "";
    int lastDot = fullName.lastIndexOf('.');
    if (lastDot > 0) {
      packageName = fullName.substring(0, lastDot);
    }
    return packageName;
  }

  public String toString() {
    return packageName() + "." + name();
  }

  public Option<BeanBuilder> beanBuilder() {
    return Some(
      ImmutableBeanBuilder.builder()
        .className("Immutable" + name())
        .packageName(packageName())
        .newBuilderMethod("builder")
        .build()
    );
  }
}
