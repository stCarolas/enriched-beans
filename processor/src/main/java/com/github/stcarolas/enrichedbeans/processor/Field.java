package com.github.stcarolas.enrichedbeans.processor;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

import com.github.stcarolas.enrichedbeans.annotations.Enrich;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;

import io.vavr.collection.List;

public class Field {
  private VariableElement origin;
  private Field(){};

  public String name(){
    return origin.getSimpleName().toString();
  }

  public TypeMirror asTypeMirror(){
    return origin.asType();
  }

  public boolean isEnriched(){
    return List.ofAll(origin.getAnnotationMirrors())
      .map(this::annotationName)
      .exists( 
        name -> Enrich.class.getCanonicalName().equals(name)
      );
  }

  public ParameterSpec asParameterSpec(){
    return List.ofAll(origin.getAnnotationMirrors())
      .filter( annotation ->
        ! Enrich.class.getCanonicalName().equals(annotationName(annotation))
      )
      .foldLeft(
        ParameterSpec
          .builder(ParameterizedTypeName.get(asTypeMirror()), name())
          .build(), 
        this::annotate
      );
  }
  
  private String annotationName(AnnotationMirror annotation){
      TypeElement annoElement = ((TypeElement)annotation.getAnnotationType().asElement());
      return annoElement.getQualifiedName().toString();
  }

  private ParameterSpec annotate(ParameterSpec parameter, AnnotationMirror annotation){
    return parameter.toBuilder()
      .addAnnotation(AnnotationSpec.get(annotation))
      .build();
  }

  public static Field from(VariableElement variable){
    Field field = new Field();
    field.origin = variable;
    return field;
  }
  
}
