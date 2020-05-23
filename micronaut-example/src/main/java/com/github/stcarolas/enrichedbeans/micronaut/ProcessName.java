package  com.github.stcarolas.enrichedbeans.micronaut;

import java.util.function.Consumer;
import java.util.function.Function;

import javax.inject.Named;

@Named("ProcessName")
public class ProcessName implements Function<String, String> {
  public String apply(String name){
    return name;
  }
}
