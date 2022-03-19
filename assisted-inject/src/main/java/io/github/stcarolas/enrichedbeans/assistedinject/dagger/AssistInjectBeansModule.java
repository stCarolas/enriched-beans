package io.github.stcarolas.enrichedbeans.assistedinject.dagger;

import dagger.Module;
import dagger.Provides;
import io.github.stcarolas.enrichedbeans.javamodel.bean.AbstractEnrichableBeanFactory;
import io.github.stcarolas.enrichedbeans.javamodel.bean.EnrichableBean;
import io.vavr.collection.List;
import io.vavr.collection.Seq;
import io.vavr.control.Option;

import static io.vavr.API.*;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;

@Module
public class AssistInjectBeansModule {

  @Provides
  public Seq<EnrichableBean> enrichableBeans(
      RoundEnvironment roundEnvironment,
      AbstractEnrichableBeanFactory beanFactory
  ){
    List<TypeElement> classes = Option(roundEnvironment)
      .map(RoundEnvironment::getRootElements)
      .flatMap(Option::of)
      .map(List::ofAll)
      .getOrElse(List())
      .map(element -> (TypeElement) element);
    return classes.flatMap(beanFactory::from);
  }

}
