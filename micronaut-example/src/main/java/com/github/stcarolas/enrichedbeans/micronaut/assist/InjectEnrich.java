package  com.github.stcarolas.enrichedbeans.micronaut.assist;

import com.github.stcarolas.enrichedbeans.annotations.Enrich;

import org.immutables.annotate.InjectAnnotation;

@InjectAnnotation(
  type=Enrich.class,
  target=InjectAnnotation.Where.FIELD
)
@interface InjectEnrich {

}
