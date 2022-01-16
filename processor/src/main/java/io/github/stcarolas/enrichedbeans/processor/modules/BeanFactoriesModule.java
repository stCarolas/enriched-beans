package io.github.stcarolas.enrichedbeans.processor.modules;

import dagger.Module;
import dagger.Provides;
import io.vavr.collection.Seq;
import javax.inject.Named;
import io.github.stcarolas.enrichedbeans.assistedinject.AssistedBeanFactory;
import io.github.stcarolas.enrichedbeans.immutablesassistedinject.AssistedImmutablesBeanFactory;
import io.github.stcarolas.enrichedbeans.javamodel.bean.BeanFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import static io.vavr.API.*;

@Module
public class BeanFactoriesModule {
  private static final Logger log = LogManager.getLogger();

  @Provides
  @Named("BeanFactories")
  public Seq<BeanFactory> beanFactories(AssistedImmutablesBeanFactory assistedBeanFactory) {
    return Seq(assistedBeanFactory);
  }
}
