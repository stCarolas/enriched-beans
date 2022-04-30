package io.github.stcarolas.enrichedbeans.micronaut.assistedinject;

import java.util.function.Function;
import javax.inject.Named;
import io.github.stcarolas.enrichedbeans.annotations.Enrich;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RichObjectWithEnrich {
  private final String name;

  @Enrich
  @Named("ProcessName")
  private final Function<String, String> processName;

  public String processName() {
    return processName.apply(name);
  }
}
