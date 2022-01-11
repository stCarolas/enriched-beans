package io.github.stcarolas.enrichedbeans.assistedinject;

import static io.vavr.API.Some;

import javax.inject.Inject;
import javax.inject.Named;
import javax.lang.model.element.Element;
import io.github.stcarolas.enrichedbeans.javamodel.Environment;
import io.github.stcarolas.enrichedbeans.javamodel.annotation.AbstractAnnotationFactory;
import io.github.stcarolas.enrichedbeans.javamodel.bean.Bean;
import io.github.stcarolas.enrichedbeans.javamodel.bean.BeanFactory;
import io.github.stcarolas.enrichedbeans.javamodel.method.AbstractMethodFactory;
import io.github.stcarolas.enrichedbeans.javamodel.variable.AbstractVariableFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import io.vavr.control.Option;

@Named
public class AssistedBeanFactory extends BeanFactory {

  private static final Logger log = LogManager.getLogger();
  private static final String DEFAULT_FACTORY_METHOD_NAME = "from";

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
  public Option<Bean> from(Element origin) {
    return Some(
      ImmutableAssistedBeanImpl.builder()
        .from(super.defaultImplementation(origin))
        .build()
    );
  }
}

