package io.github.stcarolas.enrichedbeans.micronaut.assistedinject;

import java.util.function.Function;
import javax.inject.Named;

@Named("ProcessName")
public class ProcessNameFn implements Function<String, String> {

  public String apply(String name) {
    return name;
  }

}
