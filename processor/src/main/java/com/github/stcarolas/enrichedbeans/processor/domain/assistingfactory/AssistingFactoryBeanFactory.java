package com.github.stcarolas.enrichedbeans.processor.domain.assistingfactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.lang.model.element.Modifier;
import com.github.stcarolas.enrichedbeans.processor.beans.assistingfactory.ImmutableAssistingFactoryBean;
import com.github.stcarolas.enrichedbeans.processor.java.Bean;
import com.github.stcarolas.enrichedbeans.processor.java.factories.VariableFactory;
import com.github.stcarolas.enrichedbeans.processor.spec.CanProcessBeans;
import com.github.stcarolas.enrichedbeans.processor.spec.JavaClass;
import io.vavr.control.Option;

@Named
public class AssistingFactoryBeanFactory implements CanProcessBeans {
  private static final long serialVersionUID = 7658689825243635867L;
  private String factoryMethodName;
  private Modifier visibility;
  private VariableFactory variableFactory;

  @Inject
  public AssistingFactoryBeanFactory(
    @Named("defaultFactoryMethodName") Option<String> factoryMethodName,
    @Named("visibility") Option<String> visibility,
    VariableFactory variableFactory
  ) {
    this.factoryMethodName = factoryMethodName.getOrElse("from");
    this.variableFactory = variableFactory;
    this.visibility =
      visibility.exists("package"::equals) ? Modifier.DEFAULT : Modifier.PUBLIC;
  }

  @Override
  public JavaClass apply(Bean bean) {
    return ImmutableAssistingFactoryBean.builder()
      .variableFactory(variableFactory)
      .factoryMethodName(factoryMethodName)
      .targetBean(bean)
      .visibility(visibility)
      .build();
  }
}
