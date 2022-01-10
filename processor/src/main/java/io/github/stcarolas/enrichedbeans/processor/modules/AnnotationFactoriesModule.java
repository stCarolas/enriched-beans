package com.github.stcarolas.enrichedbeans.processor.modules;

import dagger.Module;
import dagger.Provides;
import io.vavr.collection.Seq;
import javax.inject.Named;
import com.github.stcarolas.enrichedbeans.assistedinject.annotation.assisted.AssistedAnnotationFactory;
import com.github.stcarolas.enrichedbeans.assistedinject.annotation.enrich.EnrichAnnotationFactory;
import com.github.stcarolas.enrichedbeans.assistedinject.annotation.inject.InjectAnnotationFactory;
import com.github.stcarolas.enrichedbeans.assistedinject.annotation.named.NamedAnnotationFactory;
import com.github.stcarolas.enrichedbeans.javamodel.annotation.AnnotationFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import static io.vavr.API.*;

@Module
public class AnnotationFactoriesModule {
  private static final Logger log = LogManager.getLogger();

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
