package io.github.stcarolas.enrichedbeans.assistedinject;

import static io.vavr.API.Some;
import static io.vavr.API.None;
import javax.inject.Inject;
import javax.inject.Named;
import javax.lang.model.element.Element;
import io.github.stcarolas.enrichedbeans.annotations.Assisted;
import io.github.stcarolas.enrichedbeans.annotations.Enrich;
import io.github.stcarolas.enrichedbeans.javamodel.Environment;
import io.github.stcarolas.enrichedbeans.javamodel.annotation.AbstractAnnotationFactory;
import io.github.stcarolas.enrichedbeans.javamodel.bean.Bean;
import io.github.stcarolas.enrichedbeans.javamodel.bean.BeanFactory;
import io.github.stcarolas.enrichedbeans.javamodel.bean.EnrichableBean;
import io.github.stcarolas.enrichedbeans.javamodel.method.AbstractMethodFactory;
import io.github.stcarolas.enrichedbeans.javamodel.variable.AbstractVariableFactory;
import io.vavr.control.Option;

@Named
public class AssistedBeanFactory extends BeanFactory {

  @Inject
  public AssistedBeanFactory(
    AbstractVariableFactory variableFactory,
    AbstractAnnotationFactory annotationFactory,
    AbstractMethodFactory methodFactory,
    Environment env
  ) {
    super(variableFactory, annotationFactory, methodFactory, env);
  }

  @Override
  public Option<EnrichableBean> from(Element origin) {
    Bean bean = defaultImplementation(origin);
    if (bean.isAbstract()) {
      return None();
    }
    if (bean.missingAnnotation(Assisted.class) 
        && bean.missingFieldAnnotatedWith(Enrich.class)) {
      return None();
    }
    return Some(ImmutableAssistedBeanImpl.builder().from(bean).build());
  }
}
