package io.github.stcarolas.enrichedbeans.immutablesassistedinject;

import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoSet;
import io.github.stcarolas.enrichedbeans.javamodel.bean.BeanFactory;

@Module
public class AssistedInjectImmutablesModule {

  @Provides
  @IntoSet
  static BeanFactory provideOneString(AssistedImmutablesBeanFactory assistedImmutablesBeanFactory) {
    return assistedImmutablesBeanFactory;
  }
}
