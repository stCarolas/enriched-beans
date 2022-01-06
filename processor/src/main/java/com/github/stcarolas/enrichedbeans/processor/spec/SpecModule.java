package com.github.stcarolas.enrichedbeans.processor.spec;

import dagger.Provides;
import dagger.Module;
import io.vavr.collection.Seq;
import static io.vavr.API.*;
import javax.inject.Named;
import com.github.stcarolas.enrichedbeans.javamodel.CanProcessBeans;

@Module
public class SpecModule {

  @Provides
  @Named("CanProcessBeans")
  public Seq<CanProcessBeans> processors() {
    return Seq();
  }
}
