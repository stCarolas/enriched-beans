package io.github.stcarolas.enrichedbeans.micronaut.assistedinject;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.micronaut.context.ApplicationContext;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RichObjectWithEnrichTest {

  @Test
  public void testRichObjectWithEnrichFactoryGenerating(){
    ApplicationContext context = ApplicationContext.builder().start();
    RichObjectWithEnrichFactory factory = context.getBean(RichObjectWithEnrichFactory.class);
    String expectedName = "someName";
    RichObjectWithEnrich bean = factory.from(expectedName);
    Assertions.assertEquals(expectedName, bean.processName());
  }

}
