package  com.github.stcarolas.enrichedbeans.micronaut;

import org.junit.jupiter.api.Test;

import io.micronaut.context.ApplicationContext;

import static org.junit.jupiter.api.Assertions.*;

public class AssistedObjectTest {

  @Test
  public void testDI() {
    ApplicationContext context = ApplicationContext.builder().start();
    AssistedObjectFactory factory = context.getBean(AssistedObjectFactory.class);
    String expectedName = "someName";
    String actualName = factory.builder().name(expectedName).build().processName();
    assertEquals(expectedName, actualName);
  }
}
