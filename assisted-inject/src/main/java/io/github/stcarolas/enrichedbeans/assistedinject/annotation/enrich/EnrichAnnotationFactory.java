package io.github.stcarolas.enrichedbeans.assistedinject.annotation.enrich;

import static io.vavr.API.Option;

import javax.inject.Inject;
import javax.inject.Named;
import javax.lang.model.element.AnnotationMirror;

import io.github.stcarolas.enrichedbeans.annotations.Enrich;
import io.github.stcarolas.enrichedbeans.javamodel.annotation.Annotation;
import io.github.stcarolas.enrichedbeans.javamodel.annotation.AnnotationFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.vavr.control.Option;

@Named
public class EnrichAnnotationFactory extends AnnotationFactory {
  private static final Logger log = LogManager.getLogger();

  @Inject
  public EnrichAnnotationFactory(){}

  @Override
  protected Option<Annotation> from(AnnotationMirror mirror) {
    return Option(mirror)
      .map(this::asElement)
      .filter(
        anno -> anno.getQualifiedName().toString().equals(Enrich.class.getCanonicalName())
      )
      .map(
        anno -> ImmutableEnrichAnnotation.builder()
          .from(defaultImplementation(mirror))
          .build()
      );
  }
}
