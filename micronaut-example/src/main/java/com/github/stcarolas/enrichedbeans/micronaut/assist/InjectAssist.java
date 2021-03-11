package  com.github.stcarolas.enrichedbeans.micronaut.assist;

import org.immutables.annotate.InjectAnnotation;

@InjectAnnotation(
  type=InjectEnrich.class,
  target=InjectAnnotation.Where.FIELD
)
@interface InjectAssist {

}
