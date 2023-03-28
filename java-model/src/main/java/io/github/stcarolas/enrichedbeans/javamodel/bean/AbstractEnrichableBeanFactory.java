package io.github.stcarolas.enrichedbeans.javamodel.bean;

import io.github.stcarolas.enrichedbeans.javamodel.Environment;
import io.github.stcarolas.enrichedbeans.javamodel.annotation.AbstractAnnotationFactory;
import io.github.stcarolas.enrichedbeans.javamodel.method.AbstractMethodFactory;
import io.github.stcarolas.enrichedbeans.javamodel.variable.AbstractVariableFactory;
import io.vavr.collection.Seq;
import io.vavr.control.Option;
import javax.inject.Inject;
import javax.inject.Named;
import javax.lang.model.element.Element;

@Named("AbstractBeanFactory")
public class AbstractEnrichableBeanFactory extends BeanFactory {

  private Seq<BeanFactory> factories;

  @Inject
  public AbstractEnrichableBeanFactory(
    AbstractVariableFactory variableFactory,
    AbstractAnnotationFactory annotationFactory,
    AbstractMethodFactory methodFactory,
    Environment env,
    @Named("BeanFactories") Seq<BeanFactory> factories
  ) {
    super(variableFactory, annotationFactory, methodFactory, env);
    this.factories = factories;
  }

  public Option<EnrichableBean> from(Element origin) {
    return factories
      .flatMap(factory -> factory.from(origin))
      .filter(bean -> bean instanceof EnrichableBean)
      .map(bean -> (EnrichableBean) bean)
      .headOption();
  }

}
