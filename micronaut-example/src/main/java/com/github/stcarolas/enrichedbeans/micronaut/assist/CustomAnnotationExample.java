package  com.github.stcarolas.enrichedbeans.micronaut.assist;

import org.immutables.value.Value;

@Value.Immutable public interface CustomAnnotationExample {

  @InjectEnrich
  String name();

}
