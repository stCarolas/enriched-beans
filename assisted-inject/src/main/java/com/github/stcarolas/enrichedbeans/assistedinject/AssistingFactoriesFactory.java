package com.github.stcarolas.enrichedbeans.assistedinject;

import javax.inject.Inject;
import javax.inject.Named;
import javax.lang.model.element.Modifier;

import com.github.stcarolas.enrichedbeans.assistedinject.annotation.assisted.ImmutableAssistedAnnotation;
import com.github.stcarolas.enrichedbeans.javamodel.CanProcessBeans;
import com.github.stcarolas.enrichedbeans.javamodel.SourceFile;
import com.github.stcarolas.enrichedbeans.javamodel.bean.Bean;
import com.github.stcarolas.enrichedbeans.javamodel.method.constructor.Constructor;
import com.github.stcarolas.enrichedbeans.javamodel.method.constructor.ImmutableConstructImpl;
import com.squareup.javapoet.TypeSpec;

import io.vavr.Function2;
import io.vavr.control.Option;
import io.vavr.control.Try;
import static io.vavr.API.*;

@Named
public class AssistingFactoriesFactory implements CanProcessBeans {

  private final Modifier visibility;
  private final Function2<String, TypeSpec, Try<Void>> writeSourceFileFn;
  private final String factoryClassNameSuffix;

  @Inject
  public AssistingFactoriesFactory(
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
      //.className(bean.name() + factoryClassNameSuffix)
      //.fields(bean.injectedFields().map(Variable::asFieldSpec))
      .visibility(visibility)
      //.factoryMethod(bean.createFactoryMethod())
      .constructor(constructor(bean))
      .writeSourceFileFn(writeSourceFileFn)
      .build();
  }

  private Constructor constructor(Bean bean) {
    return ImmutableConstructImpl.builder()
      //.classFields(bean.injectedFields())
      .annotations(
        Seq(
          ImmutableAssistedAnnotation.builder()
            .className("Inject")
            .packageName("javax.inject")
            .build()
        )
      )
      .build();
  }

}
