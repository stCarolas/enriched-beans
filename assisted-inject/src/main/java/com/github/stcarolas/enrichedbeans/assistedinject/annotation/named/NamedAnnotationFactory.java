package com.github.stcarolas.enrichedbeans.assistedinject.annotation.named;

import javax.inject.Inject;
import javax.inject.Named;
import javax.lang.model.element.AnnotationMirror;
import com.github.stcarolas.enrichedbeans.javamodel.annotation.Annotation;
import com.github.stcarolas.enrichedbeans.javamodel.annotation.AnnotationFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import io.vavr.control.Option;
import static io.vavr.API.*;

@Named("NamedAnnotationFactory")
public class NamedAnnotationFactory extends AnnotationFactory {
  private static final Logger log = LogManager.getLogger();

  @Inject
  public NamedAnnotationFactory(){}

  @Override
  protected Option<Annotation> from(AnnotationMirror mirror) {
    return Option(mirror)
      .map(this::asElement)
      .filter(
        anno -> anno.getQualifiedName().toString().equals(Named.class.getCanonicalName())
      )
      .map(
        anno -> ImmutableNamedAnnotation.builder()
          .from(defaultImplementation(mirror))
          .build()
      );
  }
}
