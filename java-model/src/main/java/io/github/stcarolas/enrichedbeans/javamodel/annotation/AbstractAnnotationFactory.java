package com.github.stcarolas.enrichedbeans.javamodel.annotation;

import static io.vavr.API.Some;

import javax.inject.Inject;
import javax.inject.Named;
import javax.lang.model.element.AnnotationMirror;

import io.vavr.collection.Seq;
import io.vavr.control.Option;

@Named
public class AbstractAnnotationFactory extends AnnotationFactory {

  private Seq<AnnotationFactory> factories;

  @Inject
  public AbstractAnnotationFactory(
    @Named("AnnotationFactories") Seq<AnnotationFactory> factories
  ) {
    this.factories = factories;
  }

  public Option<Annotation> from(AnnotationMirror mirror) {
    return factories.flatMap(f -> f.from(mirror))
      .headOption()
      .orElse(Some(defaultImplementation(mirror)));

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

}
