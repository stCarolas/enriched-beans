package com.github.stcarolas.enrichedbeans.processor;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import com.github.stcarolas.enrichedbeans.annotations.Assisted;
import com.github.stcarolas.enrichedbeans.annotations.Enrich;
import com.github.stcarolas.enrichedbeans.processor.domain.SourceFile;
import com.github.stcarolas.enrichedbeans.processor.java.bean.Bean;
import com.github.stcarolas.enrichedbeans.processor.java.factories.BeanFactory;
import com.github.stcarolas.enrichedbeans.processor.spec.CanProcessBeans;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import dagger.Provides;
import dagger.Module;
import io.vavr.collection.List;
import io.vavr.collection.Seq;
import static io.vavr.API.*;

@Module
public class SourceGeneratingModule {

  @Provides
  public List<SourceFile> sources(
    Seq<CanProcessBeans> processors,
    BeanFactory beanFactory,
    RoundEnvironment roundEnv,
    ProcessingEnvironment processingEnv
  ) {
    return For(processors, apply(roundEnv, beanFactory))
      .yield(CanProcessBeans::apply)
      .toList();
  }

  public Seq<Bean> apply(RoundEnvironment env, BeanFactory beanFactory) {
    log.info("scaning for beans to enrich");
    return listEnrichedFields(env).distinct().map(beanFactory::from);
  }

  @SuppressWarnings("unchecked")
  private List<TypeElement> listEnrichedFields(RoundEnvironment env) {
    List.ofAll(env.getRootElements())
      .map(element -> (TypeElement) element)
      .forEach(
        element -> System.out.println(String.format("root elements: %s", element))
      );
    List<Element> targetBeans = List.ofAll(
      (java.util.Set<Element>) env.getElementsAnnotatedWith(Enrich.class)
    )
      .map(element -> element.getEnclosingElement())
      .appendAll(List.ofAll(env.getElementsAnnotatedWith(Assisted.class)));
    targetBeans.forEach(bean -> log.info("enriched bean: {}", bean.getSimpleName()));
    return targetBeans.map(element -> (TypeElement) element);
  }

  static final Logger log = LogManager.getLogger();
}
