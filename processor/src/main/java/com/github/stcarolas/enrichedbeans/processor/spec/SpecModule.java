package com.github.stcarolas.enrichedbeans.processor.spec;

import dagger.Provides;
import dagger.Module;
import io.vavr.collection.Seq;
import static io.vavr.API.*;

import com.github.stcarolas.enrichedbeans.processor.domain.assistingfactory.AssistingFactoryBeanFactory;

@Module
public class SpecModule {

  @Provides
  public Seq<CanProcessBeans> processors(
      AssistingFactoryBeanFactory assistingFactoryBean
  ) {
    return Seq(assistingFactoryBean);
  }
}
