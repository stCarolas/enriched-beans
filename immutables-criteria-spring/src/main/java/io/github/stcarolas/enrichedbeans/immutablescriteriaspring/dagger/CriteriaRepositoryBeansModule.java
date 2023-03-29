package io.github.stcarolas.enrichedbeans.immutablescriteriaspring.dagger;

import static io.vavr.API.*;

import dagger.Module;
import dagger.Provides;
import io.github.stcarolas.enrichedbeans.immutablescriteriaspring.bean.CriteriaRepositoryBean;
import io.github.stcarolas.enrichedbeans.immutablescriteriaspring.bean.CriteriaRepositoryBeanFactory;
import io.vavr.collection.List;
import io.vavr.collection.Seq;
import io.vavr.control.Option;
import javax.annotation.processing.RoundEnvironment;
import javax.inject.Named;
import javax.lang.model.element.TypeElement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Module
public class CriteriaRepositoryBeansModule {
  private Logger log = LogManager.getLogger();

  @Provides
  @Named("CriteriaRepositoryBeans")
  public Seq<CriteriaRepositoryBean> enrichableBeans(
    RoundEnvironment roundEnvironment,
    CriteriaRepositoryBeanFactory beanFactory
  ) {
    log.always().log("Processing CriteriaRepositoryBeans");
    List<TypeElement> classes = Option(roundEnvironment)
      .map(RoundEnvironment::getRootElements)
      .flatMap(Option::of)
      .map(List::ofAll)
      .peek(beans -> log.always().log("Processing beans: {}",beans))
      .getOrElse(List())
      .map(element -> (TypeElement) element);
    return classes.flatMap(beanFactory::from);
  }
}
