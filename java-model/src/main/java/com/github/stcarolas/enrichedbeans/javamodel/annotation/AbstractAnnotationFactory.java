package com.github.stcarolas.enrichedbeans.javamodel.annotation;

import javax.inject.Inject;
import javax.inject.Named;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.TypeElement;
import io.vavr.collection.HashMap;
import io.vavr.collection.Seq;

@Named
public class AbstractAnnotationFactory {

  private Seq<AnnotationFactory> factories;

  @Inject
  public AbstractAnnotationFactory(
    @Named("AnnotationFactories") Seq<AnnotationFactory> factories
  ) {
    this.factories = factories;
  }

  public Annotation from(AnnotationMirror mirror) {
    return factories.flatMap(f -> f.from(mirror))
      .headOption()
      .getOrElse(defaultImplementation(mirror));

    //if (fullName.equals(Assisted.class.getCanonicalName())){
    //annotation = ImmutableAssistedAnnotation.builder()
    //.className(className)
    //.packageName(packageName)
    //.parameters(parameters)
    //.build();
    //}
    //if (fullName.equals(Enrich.class.getCanonicalName())){
    //annotation = ImmutableEnrichAnnotation.builder()
    //.className(className)
    //.packageName(packageName)
    //.parameters(parameters)
    //.build();
    //}
    //if (fullName.equals(Named.class.getCanonicalName())){
    //annotation = ImmutableNamedAnnotation.builder()
    //.className(className)
    //.packageName(packageName)
    //.parameters(parameters)
    //.build();
    //}
    //if (fullName.equals(Inject.class.getCanonicalName())){
    //annotation = ImmutableNamedAnnotation.builder()
    //.className(className)
    //.packageName(packageName)
    //.parameters(parameters)
    //.build();
    //}
  }

  protected final Annotation defaultImplementation(AnnotationMirror mirror) {
    TypeElement annoElement = ((TypeElement) mirror.getAnnotationType().asElement());

    HashMap<String, Object> parameters = HashMap.ofAll(mirror.getElementValues())
      .mapKeys(Object::toString)
      .mapValues(AnnotationValue::getValue);

    String className = annoElement.getSimpleName().toString();
    String packageName = packageName(annoElement.getQualifiedName().toString());

    return ImmutableAnnotationImpl.builder()
      .className(className)
      .packageName(packageName)
      .parameters(parameters)
      .build();
  }

  private String packageName(String fullName) {
    return fullName.substring(0, fullName.lastIndexOf('.'));
  }
}
