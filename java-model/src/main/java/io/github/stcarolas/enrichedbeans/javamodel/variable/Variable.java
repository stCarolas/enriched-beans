package io.github.stcarolas.enrichedbeans.javamodel.variable;

import javax.lang.model.element.Modifier;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;

import org.immutables.value.Value.Immutable;
import org.immutables.vavr.encodings.VavrEncodingEnabled;

import io.github.stcarolas.enrichedbeans.javamodel.annotation.Annotation;
import io.vavr.collection.List;

public abstract class Variable {
  // TODO static factory method for creating with TypeName, Type, Class, String

  abstract public String name();

  abstract public TypeName typeName();

  abstract public List<Modifier> modifiers();

  abstract public List<Annotation> annotations();

  public ParameterSpec asParameterSpec() {
    return annotations()
      .map(Annotation::spec)
      .foldLeft(
        ParameterSpec.builder(typeName(), name()),
        ParameterSpec.Builder::addAnnotation
      )
      .build();
  }

  public FieldSpec asFieldSpec() {
    return FieldSpec.builder(typeName(), name(), modifiers().toJavaArray(Modifier.class))
      .build();
  }

  public boolean hasAnnotation(Class<?> targetAnnotationClass){
    return annotations()
      .filter(annotation -> annotation.is(targetAnnotationClass))
      .nonEmpty();
  }

  public Variable removeAnnotation(String packageName, String className) {
    return ImmutableVariableImpl.builder()
      .from(this)
      .annotations(
        annotations()
          .reject(
            annotation -> annotation.packageName().equals(packageName) &&
            annotation.className().equals(className)
          )
      )
      .build();
  }

  @Immutable
  @VavrEncodingEnabled
  public abstract static class VariableImpl extends Variable {}
}
