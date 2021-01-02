package com.github.stcarolas.enrichedbeans.processor.java;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import com.github.stcarolas.enrichedbeans.annotations.Enrich;
import com.github.stcarolas.enrichedbeans.processor.java.Annotation;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;

import org.immutables.value.Value;
import static org.immutables.value.Value.Immutable;

import io.vavr.collection.List;
import static io.vavr.API.*;

@Immutable public interface Variable {
    String name();
    TypeName type();
    List<Modifier> modifiers();
    List<Annotation> annotations();

    default ParameterSpec asParameterSpec() {
      return annotations()
        .reject(annotation -> annotation.is(Enrich.class))
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

    default boolean isEnriched() {
      return annotations().exists(annotation -> annotation.is(Enrich.class));
    }

}
