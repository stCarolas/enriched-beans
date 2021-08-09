package com.github.stcarolas.enrichedbeans.processor.domain.assistingfactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.lang.model.element.Modifier;
import com.github.stcarolas.enrichedbeans.processor.java.bean.Bean;
import com.github.stcarolas.enrichedbeans.processor.java.BeanBuilder;
import com.github.stcarolas.enrichedbeans.processor.java.Variable;
import com.github.stcarolas.enrichedbeans.processor.java.annotation.ImmutableDefaultAnnotationImpl;
import com.github.stcarolas.enrichedbeans.processor.java.annotation.assisted.AssistedAnnotation;
import com.github.stcarolas.enrichedbeans.processor.java.factories.VariableFactory;
import com.github.stcarolas.enrichedbeans.processor.spec.CanProcessBeans;
import com.github.stcarolas.enrichedbeans.processor.spec.method.Constructor;
import com.github.stcarolas.enrichedbeans.processor.spec.method.ImmutableConstructor;
import com.github.stcarolas.enrichedbeans.processor.spec.method.ImmutableFactoryMethod;
import com.github.stcarolas.enrichedbeans.processor.spec.method.ImmutableFactoryMethodUsingBuilder;
import com.github.stcarolas.enrichedbeans.processor.spec.method.api.MethodWithSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeSpec;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.stcarolas.enrichedbeans.processor.domain.SourceFile;
import io.vavr.Function2;
import io.vavr.control.Option;
import io.vavr.control.Try;
import static io.vavr.API.*;

@Named
public class AssistingFactoryBeanFactory implements CanProcessBeans {
  private static final long serialVersionUID = 7658689825243635867L;
  private static final Logger log = LogManager.getLogger();

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
    this.visibility = visibility.exists("package"::equals)
      ? Modifier.DEFAULT 
      : Modifier.PUBLIC;
    this.writeSourceFileFn = writeSourceFileFn;
  }

  @Override
  public SourceFile apply(Bean bean) {
    return ImmutableAssistingFactoryBean.builder()
      .packageName(bean.packageName())
      .name(bean.name() + "Factory")
      .fields(bean.injectedFields().map(Variable::asFieldSpec))
      .visibility(visibility)
      .factoryMethod(bean.createFactoryMethod())
      .constructor(constructor(bean))
      .writeSourceFileFn(writeSourceFileFn)
      .build();
  }

  public Constructor constructor(Bean bean) {
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
