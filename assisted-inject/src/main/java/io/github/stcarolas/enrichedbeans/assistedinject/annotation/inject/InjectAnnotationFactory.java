package  io.github.stcarolas.enrichedbeans.assistedinject.annotation.inject;

import static io.vavr.API.Option;

import javax.inject.Inject;
import javax.inject.Named;
import javax.lang.model.element.AnnotationMirror;

import io.github.stcarolas.enrichedbeans.javamodel.annotation.Annotation;
import io.github.stcarolas.enrichedbeans.javamodel.annotation.AnnotationFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.vavr.control.Option;

@Named
public class InjectAnnotationFactory extends AnnotationFactory {

  private static final Logger log = LogManager.getLogger();

  @Inject
  public InjectAnnotationFactory(){}

  @Override
  protected Option<Annotation> from(AnnotationMirror mirror) {
    return Option(mirror)
      .map(this::asElement)
      .filter(
        anno -> anno.getQualifiedName().toString().equals(Inject.class.getCanonicalName())
      )
      .map(
        anno -> ImmutableInjectAnnotation.builder()
          .from(defaultImplementation(mirror))
          .build()
      );
  }
}
