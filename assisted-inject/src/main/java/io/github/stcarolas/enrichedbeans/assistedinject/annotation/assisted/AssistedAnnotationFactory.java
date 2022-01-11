package io.github.stcarolas.enrichedbeans.assistedinject.annotation.assisted;

import javax.inject.Inject;
import javax.inject.Named;
import javax.lang.model.element.AnnotationMirror;
import io.github.stcarolas.enrichedbeans.annotations.Assisted;
import io.github.stcarolas.enrichedbeans.javamodel.annotation.Annotation;
import io.github.stcarolas.enrichedbeans.javamodel.annotation.AnnotationFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import io.vavr.control.Option;
import static io.vavr.API.*;

@Named
public class AssistedAnnotationFactory extends AnnotationFactory {
  private static final Logger log = LogManager.getLogger();
  
  @Inject
  public AssistedAnnotationFactory() {}

  @Override
  protected Option<Annotation> from(AnnotationMirror mirror) {
    return Option(mirror)
      .map(this::asElement)
      .filter(
        anno -> anno.getQualifiedName()
          .toString()
          .equals(Assisted.class.getCanonicalName())
      )
      .map(
        anno -> ImmutableAssistedAnnotation.builder()
          .from(defaultImplementation(mirror))
          .build()
      );
  }
}
