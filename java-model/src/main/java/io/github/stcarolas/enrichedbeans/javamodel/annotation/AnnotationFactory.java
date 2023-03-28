package io.github.stcarolas.enrichedbeans.javamodel.annotation;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.TypeElement;

import io.vavr.collection.HashMap;
import io.vavr.control.Option;

public abstract class AnnotationFactory {

  abstract protected Option<Annotation> from(AnnotationMirror mirror);

  protected final Annotation defaultImplementation(AnnotationMirror mirror) {
    TypeElement annoElement = asElement(mirror);

    HashMap<String, Object> parameters = HashMap.ofAll(mirror.getElementValues())
      .mapKeys(Object::toString)
      .mapValues(AnnotationValue::getValue);

    String className = annoElement.getSimpleName().toString();
    String packageName = packageName(annoElement.getQualifiedName().toString());

    ImmutableAnnotationImpl annotation = ImmutableAnnotationImpl.builder()
      .className(className)
      .packageName(packageName)
      .parameters(parameters)
      .build();
    return annotation;
  }

  protected final TypeElement asElement(AnnotationMirror mirror){
    return ((TypeElement) mirror.getAnnotationType().asElement());
  }

  protected final String packageName(String fullName) {
    return fullName.substring(0, fullName.lastIndexOf('.'));
  }
}
