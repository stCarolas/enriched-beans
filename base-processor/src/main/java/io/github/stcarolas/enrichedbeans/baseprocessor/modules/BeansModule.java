package io.github.stcarolas.enrichedbeans.baseprocessor.modules;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.inject.Named;
import javax.lang.model.element.TypeElement;

import io.github.stcarolas.enrichedbeans.javamodel.bean.AbstractEnrichableBeanFactory;
import io.github.stcarolas.enrichedbeans.javamodel.bean.Bean;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import dagger.Provides;
import dagger.Module;
import io.vavr.collection.List;
import io.vavr.collection.Seq;

@Module
public class BeansModule {
  static final Logger log = LogManager.getLogger();

  @Provides
  @Named("Beans")
  public Seq<Bean> beans(
    AbstractEnrichableBeanFactory beanFactory,
    RoundEnvironment roundEnv,
    ProcessingEnvironment processingEnv
  ) {
    return apply(roundEnv, beanFactory);
  }

  public Seq<Bean> apply(RoundEnvironment env, AbstractEnrichableBeanFactory beanFactory) {
    log.info("scaning for beans to enrich");
    return listRootElements(env)
      .distinct()
      .flatMap(beanFactory::from);
  }

  private List<TypeElement> listRootElements(RoundEnvironment env) {
    if (log.isDebugEnabled()){
      log.info(
        "root elements: {}", 
        List.ofAll(env.getRootElements())
      );
    }
    return List.ofAll(env.getRootElements())
      .map(element -> (TypeElement) element);
  }

}
