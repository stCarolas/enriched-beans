package  com.github.stcarolas.enrichedbeans.processor.java.factories;

import javax.inject.Inject;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.TypeElement;

import com.github.stcarolas.enrichedbeans.processor.java.Annotation;
import com.github.stcarolas.enrichedbeans.processor.java.ImmutableAnnotation;

import io.vavr.collection.HashMap;

public class AnnotationFactory {

  public Annotation from(AnnotationMirror mirror){
    TypeElement annoElement = ((TypeElement) mirror.getAnnotationType()
        .asElement());

    HashMap<String, Object> parameters = HashMap.ofAll(mirror.getElementValues())
      .mapKeys(Object::toString)
      .mapValues(AnnotationValue::getValue);

    return ImmutableAnnotation.builder()
      .className(annoElement.getSimpleName().toString())
      .packageName(packageName(annoElement.getQualifiedName().toString()))
      .parameters(parameters)
      .build();
  }

  private String packageName(String fullName){
    return fullName.substring(0,fullName.lastIndexOf('.'));
  }

  @Inject public AnnotationFactory(){};

}
