package io.github.stcarolas.enrichedbeans.immutablescriteriaspring.dagger;

import dagger.Module;
import dagger.Provides;
import io.vavr.collection.Seq;
import javax.inject.Named;

import io.github.stcarolas.enrichedbeans.immutablescriteriaspring.annotation.CriteriaRepositoryAnnotationFactory;
import io.github.stcarolas.enrichedbeans.javamodel.annotation.AnnotationFactory;
import static io.vavr.API.*;

@Module
public class AnnotationFactoriesModule {

  @Provides
  @Named("AnnotationFactories")
  public static Seq<AnnotationFactory> annotationFactories(
    CriteriaRepositoryAnnotationFactory criteriaAnnotationFactory
  ) {
    return Seq(
      criteriaAnnotationFactory
    );
  }
}

