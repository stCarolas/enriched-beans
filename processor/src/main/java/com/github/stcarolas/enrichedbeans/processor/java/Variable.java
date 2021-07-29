package com.github.stcarolas.enrichedbeans.processor.java;

import javax.lang.model.element.Modifier;
import com.github.stcarolas.enrichedbeans.processor.java.annotation.Annotation;
import com.github.stcarolas.enrichedbeans.processor.java.annotation.enrich.EnrichAnnotation;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;

import org.immutables.value.Value.Default;

import static org.immutables.value.Value.Immutable;

import io.vavr.collection.List;

@Immutable public interface Variable {

    String name();
    TypeName type();

    @Default default List<Modifier> modifiers(){
      return List.empty();
    }

    @Default default List<Annotation> annotations(){
      return List.empty();
    }

    default ParameterSpec asParameterSpec() {
      return annotations()
        .reject(annotation -> annotation instanceof EnrichAnnotation)
        .map(Annotation::spec)
        .foldLeft(
          ParameterSpec.builder(type(), name()),
          ParameterSpec.Builder::addAnnotation
        )
        .build();
    }

    default FieldSpec asFieldSpec() {
      return FieldSpec.builder(
        type(),
        name(),
        modifiers().toJavaArray(Modifier.class)
      ).build();
    }

    default String accessor(){
      return name();
    }

}
