package com.github.stcarolas.enrichedbeans.processor.java.annotation;

import javax.inject.Inject;
import javax.inject.Named;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.TypeElement;

import com.github.stcarolas.enrichedbeans.annotations.Assisted;
import com.github.stcarolas.enrichedbeans.annotations.Enrich;
import com.github.stcarolas.enrichedbeans.processor.java.annotation.assisted.ImmutableAssistedAnnotation;
import com.github.stcarolas.enrichedbeans.processor.java.annotation.enrich.ImmutableEnrichAnnotation;

import io.vavr.collection.HashMap;

@Named
public class AnnotationFactory {

  @Inject
  public AnnotationFactory() {}

  public Annotation from(AnnotationMirror mirror) {
    TypeElement annoElement = ((TypeElement) mirror.getAnnotationType().asElement());

    HashMap<String, Object> parameters = HashMap.ofAll(mirror.getElementValues())
      .mapKeys(Object::toString)
      .mapValues(AnnotationValue::getValue);

    String className = annoElement.getSimpleName().toString();
    String packageName = packageName(annoElement.getQualifiedName().toString());
    String fullName = packageName + "." + className;

    Annotation annotation = ImmutableDefaultAnnotationImpl.builder()
      .className(className)
      .packageName(packageName)
      .parameters(parameters)
      .build();

    if (fullName.equals(Assisted.class.getCanonicalName())){
      annotation = ImmutableAssistedAnnotation.builder()
        .className(className)
        .packageName(packageName)
        .parameters(parameters)
        .build();
    }

    if (fullName.equals(Enrich.class.getCanonicalName())){
      annotation = ImmutableEnrichAnnotation.builder()
        .className(className)
        .packageName(packageName)
        .parameters(parameters)
        .build();
    }

    return annotation;
  }

  private String packageName(String fullName) {
    return fullName.substring(0, fullName.lastIndexOf('.'));
  }
}
