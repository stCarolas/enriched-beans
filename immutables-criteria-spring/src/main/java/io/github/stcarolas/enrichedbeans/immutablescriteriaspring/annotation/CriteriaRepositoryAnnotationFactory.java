package io.github.stcarolas.enrichedbeans.immutablescriteriaspring.annotation;

import static io.vavr.API.*;

import io.github.stcarolas.enrichedbeans.javamodel.annotation.Annotation;
import io.github.stcarolas.enrichedbeans.javamodel.annotation.AnnotationFactory;
import io.vavr.control.Option;
import javax.inject.Inject;
import javax.inject.Named;
import javax.lang.model.element.AnnotationMirror;

@Named("CriteriaRepositoryAnnotationFactory")
public class CriteriaRepositoryAnnotationFactory extends AnnotationFactory {

  @Inject
  public CriteriaRepositoryAnnotationFactory() {}

  @Override
  protected Option<Annotation> from(AnnotationMirror mirror) {
    return Option(mirror)
      .map(this::asElement)
      .filter(anno ->
        CriteriaRepositoryAnnotation.ANNOTATION_CANONICAL_NAME.equals(
          anno.getQualifiedName().toString()
        )
      )
      .map(anno ->
        ImmutableCriteriaRepositoryAnnotation
          .builder()
          .from(defaultImplementation(mirror))
          .build()
      );
  }
}
