package com.github.stcarolas.enrichedbeans.micronaut.simple;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.micronaut.context.ApplicationContext;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class  RichObjectTest {

  @Test
  public void testDI(){
    ApplicationContext context = ApplicationContext.builder().start();
    RichObjectFactory factory = context.getBean(RichObjectFactory.class);
    String expectedName = "someName";
    String actualName = factory.from(expectedName).processName();
    Assertions.assertEquals(expectedName, actualName);
  }

}
