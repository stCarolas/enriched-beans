package  com.github.stcarolas.enrichedbeans.micronaut;

import com.github.stcarolas.enrichedbeans.annotations.Implement;

public class AbstractRichObject {

  private final String name = "name";
  
  @Implement
  public interface Behavior {
    String processName(String name);
  }

}
