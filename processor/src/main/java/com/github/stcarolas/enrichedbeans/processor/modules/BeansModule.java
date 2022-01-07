package com.github.stcarolas.enrichedbeans.processor.modules;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.inject.Named;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import com.github.stcarolas.enrichedbeans.annotations.Assisted;
import com.github.stcarolas.enrichedbeans.annotations.Enrich;
import com.github.stcarolas.enrichedbeans.javamodel.bean.AbstractBeanFactory;
import com.github.stcarolas.enrichedbeans.javamodel.bean.Bean;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import dagger.Provides;
import dagger.Module;
import io.vavr.collection.List;
import io.vavr.collection.Seq;

import static io.vavr.API.*;

@Module
public class BeansModule {
  static final Logger log = LogManager.getLogger();

  @Provides
  @Named("Beans")
  public Seq<Bean> beans(
    AbstractBeanFactory beanFactory,
    RoundEnvironment roundEnv,
    ProcessingEnvironment processingEnv
  ) {
    return apply(roundEnv, beanFactory);
  }

  public Seq<Bean> apply(RoundEnvironment env, AbstractBeanFactory beanFactory) {
    log.info("scaning for beans to enrich");
    return listEnrichedFields(env)
      .distinct()
      .flatMap(beanFactory::from);
  }

  @SuppressWarnings("unchecked")
  private List<TypeElement> listEnrichedFields(RoundEnvironment env) {
    //if (log.isDebugEnabled()){
      log.info(
        "root elements: {}", 
        List.ofAll(env.getRootElements())
      );
    //}
    List<Element> targetBeans = List.ofAll(
      (java.util.Set<Element>) env.getElementsAnnotatedWith(Enrich.class)
    )
      .map(element -> element.getEnclosingElement())
      .appendAll(List.ofAll(env.getElementsAnnotatedWith(Assisted.class)));
    targetBeans.forEach(bean -> log.debug("bean to enrich: {}", bean.getSimpleName()));
    return targetBeans.map(element -> (TypeElement) element);
  }
}
