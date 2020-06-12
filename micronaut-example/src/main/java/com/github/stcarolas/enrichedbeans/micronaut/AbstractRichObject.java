package  com.github.stcarolas.enrichedbeans.micronaut;

import javax.inject.Named;

import com.github.stcarolas.enrichedbeans.annotations.Implement;

public class AbstractRichObject {

  private final String name = "name";
  
  @Implement
  public interface Behavior {
    @Named("ProcessName")
    String processName(String name);
  }

}
