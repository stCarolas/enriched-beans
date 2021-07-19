package com.github.stcarolas.enrichedbeans.processor.domain.assistingfactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.lang.model.element.Modifier;
import com.github.stcarolas.enrichedbeans.processor.java.Bean;
import com.github.stcarolas.enrichedbeans.processor.java.factories.VariableFactory;
import com.github.stcarolas.enrichedbeans.processor.spec.CanProcessBeans;
import com.squareup.javapoet.TypeSpec;
import com.github.stcarolas.enrichedbeans.processor.domain.SourceFile;
import io.vavr.Function2;
import io.vavr.control.Option;
import io.vavr.control.Try;

@Named
public class AssistingFactoryBeanFactory implements CanProcessBeans {
  private static final long serialVersionUID = 7658689825243635867L;

  private String factoryMethodName;
  private Modifier visibility;
  private VariableFactory variableFactory;
  private Function2<String, TypeSpec, Try<Void>> writeSourceFileFn;

  @Inject
  public AssistingFactoryBeanFactory(
    @Named("defaultFactoryMethodName") Option<String> factoryMethodName,
    @Named("defaultVisibility") Option<String> visibility,
    @Named("WriteSourceFileFn") Function2<String, TypeSpec, Try<Void>> writeSourceFileFn,
    VariableFactory variableFactory
  ) {
    this.factoryMethodName = factoryMethodName.getOrElse("from");
    this.variableFactory = variableFactory;
    this.visibility =
      visibility.exists("package"::equals) ? Modifier.DEFAULT : Modifier.PUBLIC;
    this.writeSourceFileFn = writeSourceFileFn;
  }

  @Override
  public SourceFile apply(Bean bean) {
    return ImmutableAssistingFactoryBean.builder()
      .packageName(bean.packageName())
      .name(bean.name() + "Factory")
      .variableFactory(variableFactory)
      .factoryMethodName(factoryMethodName)
      .targetBean(bean)
      .visibility(visibility)
      .writeSourceFileFn(writeSourceFileFn)
      .build();
  }
}
