package com.github.stcarolas.enrichedbeans.processor;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import com.github.stcarolas.enrichedbeans.annotations.Enrich;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import io.vavr.collection.List;
import static io.vavr.API.*;

public class Field {
    private String name;
    private TypeName type;
    private List<Modifier> modifiers;
    private List<AnnotationMirror> annotationMirrors;

    private Field() {}

    public String name() {
        return name;
    }

    public TypeName typeName() {
        return type;
    }

    public ParameterSpec asParameterSpec() {
        return List.ofAll(annotationMirrors)
            .filter(
                annotation -> !Enrich
                .class
                    .getCanonicalName()
                    .equals(annotationName(annotation))
            )
            .foldLeft(ParameterSpec.builder(typeName(), name()).build(), this::annotate);
    }

    private String annotationName(AnnotationMirror annotation) {
        TypeElement annoElement = ((TypeElement) annotation.getAnnotationType()
            .asElement());
        return annoElement.getQualifiedName().toString();
    }

    private ParameterSpec annotate(ParameterSpec parameter, AnnotationMirror annotation) {
        return parameter.toBuilder()
            .addAnnotation(AnnotationSpec.get(annotation))
            .build();
    }

    public static Field from(VariableElement variable) {
        Field field = new Field();
        field.name = variable.getSimpleName().toString();
        field.type = ParameterizedTypeName.get(variable.asType());
        field.annotationMirrors = List.ofAll(variable.getAnnotationMirrors());
        return field;
    }

    public boolean isEnriched() {
        return List.ofAll(annotationMirrors)
            .map(this::annotationName)
            .exists(name -> Enrich.class.getCanonicalName().equals(name));
    }

    public static Field from(
        String name,
        TypeName type,
        List<AnnotationMirror> annotations
    ) {
        return from(name, type, annotations, List());
    }

    public static Field from(
        String name,
        TypeName type,
        List<AnnotationMirror> annotations,
        List<Modifier> modifiers
    ) {
        Field field = new Field();
        field.name = name;
        field.type = type;
        field.modifiers = modifiers;
        field.annotationMirrors = annotations;
        return field;
    }

    public Field withName(
        String name
    ) {
        return from(name, this.type, this.annotationMirrors, this.modifiers);
    }

    public Field withType(
        TypeName type
    ) {
        return from(this.name, type, this.annotationMirrors, this.modifiers);
    }

    public Field withAnnotations(
      List<AnnotationMirror> annotationMirrors
    ) {
        return from(this.name, this.type, annotationMirrors, this.modifiers);
    }

    public Field withModifiers(
      List<Modifier> modifiers
    ) {
        return from(this.name, this.type, this.annotationMirrors, modifiers);
    }
}
