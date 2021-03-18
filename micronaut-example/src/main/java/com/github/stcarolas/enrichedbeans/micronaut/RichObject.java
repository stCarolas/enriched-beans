package  com.github.stcarolas.enrichedbeans.micronaut;

import java.util.function.Function;
import javax.inject.Named;

import com.github.stcarolas.enrichedbeans.annotations.Enrich;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RichObject {

  private final String name;

  @Enrich @Named("ProcessName")
  private final Function<String, String> processName;

  public String processName(){
    return processName.apply(name);
  }

}
