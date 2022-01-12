package io.github.stcarolas.enrichedbeans.micronaut.simple;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.micronaut.context.ApplicationContext;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RichObjectTest {

  @Test
  public void testDI(){
    ApplicationContext context = ApplicationContext.builder().start();
    RichObjectFactory factory = context.getBean(RichObjectFactory.class);
    String expectedName = "someName";
    RichObject bean = factory.from(expectedName);
    Assertions.assertEquals(expectedName, bean.processName());
    Assertions.assertEquals(expectedName, bean.processName2());
  }

}
