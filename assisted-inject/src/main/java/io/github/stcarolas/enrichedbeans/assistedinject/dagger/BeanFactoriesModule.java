package io.github.stcarolas.enrichedbeans.assistedinject.dagger;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import io.github.stcarolas.enrichedbeans.assistedinject.AssistedBeanFactory;
import io.github.stcarolas.enrichedbeans.javamodel.bean.BeanFactory;
import io.vavr.collection.Seq;
import static io.vavr.API.Seq;

@Module
public class BeanFactoriesModule {

  @Provides
  @Named("BeanFactories")
  static Seq<BeanFactory> provideAssistedBeanFactory(
    AssistedBeanFactory assistedBeanFactory
  ) {
    return Seq(assistedBeanFactory);
  }
}
