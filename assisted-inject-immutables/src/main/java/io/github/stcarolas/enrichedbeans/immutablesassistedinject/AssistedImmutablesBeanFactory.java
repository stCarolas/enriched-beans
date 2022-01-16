package io.github.stcarolas.enrichedbeans.immutablesassistedinject;

import static io.vavr.API.Some;
import javax.inject.Inject;
import javax.inject.Named;
import javax.lang.model.element.Element;
import io.github.stcarolas.enrichedbeans.assistedinject.AssistedBeanFactory;
import io.github.stcarolas.enrichedbeans.assistedinject.ImmutableAssistedBeanImpl;
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
public class AssistedImmutablesBeanFactory extends AssistedBeanFactory {
  private static final Logger log = LogManager.getLogger();

  @Inject
  public AssistedImmutablesBeanFactory(
    AbstractVariableFactory variableFactory,
    AbstractAnnotationFactory annotationFactory,
    AbstractMethodFactory methodFactory,
    Environment env
  ) {
    super(variableFactory, annotationFactory, methodFactory, env);
  }

  @Override
  public Option<Bean> from(Element origin) {
    Bean bean = super.defaultImplementation(origin);
    return bean.annotations()
      .find(
        anno -> "org.immutables.value.Value".equals(anno.packageName()) &&
        "Immutable".equals(anno.className())
      )
      .map(
        immutable -> (Bean) ImmutableAssistedImmutablesBeanImpl.builder()
          .from(bean)
          .build()
      )
      .orElse(() -> Some(ImmutableAssistedBeanImpl.builder().from(bean).build()));
  }
}
