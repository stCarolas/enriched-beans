package io.github.stcarolas.enrichedbeans.immutablescriteriaspring.dagger;

import static io.vavr.API.*;

import dagger.Module;
import dagger.Provides;
import io.github.stcarolas.enrichedbeans.immutablescriteriaspring.bean.CriteriaRepositoryBeanFactory;
import io.github.stcarolas.enrichedbeans.javamodel.bean.BeanFactory;
import io.vavr.collection.Seq;
import javax.inject.Named;

@Module
public class BeanFactoriesModule {

  @Provides
  @Named("BeanFactories")
  public static Seq<BeanFactory> provideAssistedBeanFactory(
    CriteriaRepositoryBeanFactory criteriaRepositoryBeanFactory
  ) {
    return Seq(criteriaRepositoryBeanFactory);
  }
}
