package io.github.stcarolas.enrichedbeans.assistedinject.dagger;

import dagger.Module;
import dagger.Provides;
import io.vavr.collection.Seq;
import javax.inject.Named;
import io.github.stcarolas.enrichedbeans.assistedinject.annotation.assisted.AssistedAnnotationFactory;
import io.github.stcarolas.enrichedbeans.assistedinject.annotation.enrich.EnrichAnnotationFactory;
import io.github.stcarolas.enrichedbeans.assistedinject.annotation.inject.InjectAnnotationFactory;
import io.github.stcarolas.enrichedbeans.assistedinject.annotation.named.NamedAnnotationFactory;
import io.github.stcarolas.enrichedbeans.javamodel.annotation.AnnotationFactory;
import static io.vavr.API.*;

@Module
public class AnnotationFactoriesModule {

  @Provides
  @Named("AnnotationFactories")
  public Seq<AnnotationFactory> annotationFactories(
    NamedAnnotationFactory namedAnnotationFactory,
    AssistedAnnotationFactory assistedAnnotationFactory,
    EnrichAnnotationFactory enrichAnnotationFactory,
    InjectAnnotationFactory injectAnnotationFactory
  ) {
    return Seq(
      namedAnnotationFactory,
      assistedAnnotationFactory,
      enrichAnnotationFactory,
      injectAnnotationFactory
    );
  }
}
