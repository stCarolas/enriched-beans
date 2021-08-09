package com.github.stcarolas.enrichedbeans.processor.domain.assistingfactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.lang.model.element.Modifier;
import com.github.stcarolas.enrichedbeans.processor.java.bean.Bean;
import com.github.stcarolas.enrichedbeans.processor.java.Variable;
import com.github.stcarolas.enrichedbeans.processor.java.annotation.ImmutableDefaultAnnotationImpl;
import com.github.stcarolas.enrichedbeans.processor.java.factories.VariableFactory;
import com.github.stcarolas.enrichedbeans.processor.spec.CanProcessBeans;
import com.github.stcarolas.enrichedbeans.processor.spec.method.Constructor;
import com.github.stcarolas.enrichedbeans.processor.spec.method.ImmutableConstructor;
import com.squareup.javapoet.TypeSpec;

import com.github.stcarolas.enrichedbeans.processor.domain.SourceFile;
import io.vavr.Function2;
import io.vavr.control.Option;
import io.vavr.control.Try;
import static io.vavr.API.*;

@Named
public class AssistingFactoryBeanFactory implements CanProcessBeans {
  private static final long serialVersionUID = 7658689825243635867L;

  private final Modifier visibility;
  private final Function2<String, TypeSpec, Try<Void>> writeSourceFileFn;
  private final String factoryClassNameSuffix;

  @Inject
  public AssistingFactoryBeanFactory(
    @Named("visibility") Option<String> visibility,
    @Named("factoryClassNameSuffix") Option<String> factoryClassNameSuffix,
    @Named("WriteSourceFileFn") Function2<String, TypeSpec, Try<Void>> writeSourceFileFn
  ) {
    this.visibility = visibility.exists("package"::equals)
      ? Modifier.DEFAULT 
      : Modifier.PUBLIC;
    this.writeSourceFileFn = writeSourceFileFn;
    this.factoryClassNameSuffix = factoryClassNameSuffix.getOrElse("Factory");
  }

  @Override
  public SourceFile apply(Bean bean) {
    return ImmutableAssistingFactoryBean.builder()
      .packageName(bean.packageName())
      .name(bean.name() + factoryClassNameSuffix)
      .fields(bean.injectedFields().map(Variable::asFieldSpec))
      .visibility(visibility)
      .factoryMethod(bean.createFactoryMethod())
      .constructor(constructor(bean))
      .writeSourceFileFn(writeSourceFileFn)
      .build();
  }

  private Constructor constructor(Bean bean) {
    return ImmutableConstructor.builder()
      .classFields(bean.injectedFields())
      .annotations(
        Seq(
          ImmutableDefaultAnnotationImpl.builder()
            .className("Inject")
            .packageName("javax.inject")
            .build()
        )
      )
      .build();
  }

}
