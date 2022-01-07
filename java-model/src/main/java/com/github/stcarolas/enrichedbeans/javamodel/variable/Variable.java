package com.github.stcarolas.enrichedbeans.javamodel.variable;

import javax.lang.model.element.Modifier;
import com.github.stcarolas.enrichedbeans.javamodel.annotation.Annotation;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import org.immutables.vavr.encodings.VavrEncodingEnabled;
import static org.immutables.value.Value.Immutable;
import io.vavr.collection.List;

public abstract class Variable {

  abstract public String name();

  abstract public TypeName type();

  abstract public List<Modifier> modifiers();

  abstract public List<Annotation> annotations();

  public ParameterSpec asParameterSpec() {
    return annotations()//.reject(annotation -> annotation instanceof EnrichAnnotation)
    .map(Annotation::spec).foldLeft(ParameterSpec.builder(type(), name()), ParameterSpec.Builder::addAnnotation).build();
  }

  public FieldSpec asFieldSpec() {
    return FieldSpec.builder(type(), name(), modifiers().toJavaArray(Modifier.class))
      .build();
  }

  public String accessor() {
    return name();
  }

  public Variable removeAnnotation(String packageName, String className) {
    return ImmutableVariableImpl.builder()
      .from(this)
      .annotations(
        annotations()
          .reject(
            annotation -> annotation.packageName().equals(packageName) 
              && annotation.className().equals(className)
          )
      )
      .build();
  }

  @Immutable
  @VavrEncodingEnabled
  public abstract static class VariableImpl extends Variable {}
}
