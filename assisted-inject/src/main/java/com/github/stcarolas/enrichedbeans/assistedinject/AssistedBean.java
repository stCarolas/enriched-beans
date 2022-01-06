package com.github.stcarolas.enrichedbeans.assistedinject;

import java.util.function.Predicate;
import javax.lang.model.element.Modifier;
import com.github.stcarolas.enrichedbeans.assistedinject.annotation.assisted.AssistedAnnotation;
import com.github.stcarolas.enrichedbeans.assistedinject.annotation.assisted.ImmutableAssistedAnnotation;
import com.github.stcarolas.enrichedbeans.assistedinject.annotation.enrich.EnrichAnnotation;
import com.github.stcarolas.enrichedbeans.assistedinject.annotation.inject.InjectAnnotation;
import com.github.stcarolas.enrichedbeans.assistedinject.annotation.named.NamedAnnotation;
import com.github.stcarolas.enrichedbeans.assistedinject.method.ImmutableFactoryMethodUsingBuilder;
import com.github.stcarolas.enrichedbeans.assistedinject.method.ImmutableFactoryMethodUsingConstructor;
import com.github.stcarolas.enrichedbeans.javamodel.SourceFile;
import com.github.stcarolas.enrichedbeans.javamodel.bean.Bean;
import com.github.stcarolas.enrichedbeans.javamodel.method.Method;
import com.github.stcarolas.enrichedbeans.javamodel.method.constructor.Constructor;
import com.github.stcarolas.enrichedbeans.javamodel.method.constructor.ImmutableConstructImpl;
import com.github.stcarolas.enrichedbeans.javamodel.variable.AbstractVariableFactory;
import com.github.stcarolas.enrichedbeans.javamodel.variable.ImmutableVariableImpl;
import com.github.stcarolas.enrichedbeans.javamodel.variable.Variable;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeSpec;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import io.vavr.Function2;
import io.vavr.collection.Seq;
import io.vavr.control.Option;
import io.vavr.control.Try;
import static io.vavr.API.*;

public abstract class AssistedBean extends Bean {
  private static final Logger log = LogManager.getLogger();
  private static final String DEFAULT_FACTORY_METHOD_NAME = "from";
  private static final String DEFAULT_BUILDER_METHOD_NAME = "builder";

  abstract Modifier factoryVisibility();

  abstract Function2<String, TypeSpec, Try<Void>> writeSourceFileFn();

  abstract public Option<String> factoryMethodName();

  abstract public Option<String> factoryClassNameSuffix();

  abstract public Option<BeanBuilder> beanBuilder();

  abstract public AbstractVariableFactory variableFactory();

  @Override
  public Try<Seq<SourceFile>> process() {
    return createFactoryMethod()
      .map(
        method -> Seq(
          ImmutableAssistingFactoryBean.builder()
            .packageName(packageName())
            .className(className() + factoryClassNameSuffix())
            .fields(injectedFields().map(Variable::asFieldSpec))
            .visibility(factoryVisibility())
            .factoryMethod(method)
            .constructor(constructorForFactory())
            .writeSourceFileFn(writeSourceFileFn())
            .build()
        )
      );
  }

  private Constructor constructorForFactory() {
    return ImmutableConstructImpl.builder() //.classFields(bean.injectedFields())
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

  public boolean assistAllInjectedFields() {
    return annotations()
      .find(anno -> anno instanceof AssistedAnnotation)
      .map(anno -> (AssistedAnnotation) anno)
      .map(AssistedAnnotation::assistAllInjectedFields)
      .getOrElse(false);
  }

  public Try<Method> createFactoryMethod() {
    Option<BeanBuilder> builder = useBuilder() ? beanBuilder() : None();
    return builder.map(this::factoryMethodUsingBuilder)
      .getOrElse(this::factoryMethodUsingConstructor);
  }

  private boolean useBuilder() {
    return annotations()
      .find(anno -> anno instanceof AssistedAnnotation)
      .map(anno -> (AssistedAnnotation) anno)
      .map(AssistedAnnotation::useBuilder)
      .getOrElse(false);
  }

  private Try<Method> factoryMethodUsingConstructor() {
    log.debug("Construct factory method using constructor with");
    return asType()
      .map(
        type -> ImmutableFactoryMethodUsingConstructor.builder()
          .name(factoryMethodName().getOrElse(DEFAULT_FACTORY_METHOD_NAME))
          .returnType(type)
          .parameters(notInjectedFields())
          .injectedFields(injectedFields())
          .build()
      );
  }

  private Try<Method> factoryMethodUsingBuilder(BeanBuilder beanBuilder) {
    log.debug("Construct factory method using builder");
    return Success(
      ImmutableFactoryMethodUsingBuilder.builder()
        .beanBuilder(beanBuilder)
        .name(DEFAULT_BUILDER_METHOD_NAME)
        .returnType(
          ClassName.get(
            String.format("%s.%s", beanBuilder.packageName(), beanBuilder.className()),
            "Builder"
          )
        )
        .parameters(Seq())
        .injectedFields(injectedFields())
        .build()
    );
  }

  public Seq<Variable> notInjectedFields() {
    return fields().reject(shouldBeInjected(assistAllInjectedFields()));
  }

  public Seq<Variable> injectedFields() {
    Seq<Variable> injectedByMethods = methods()
      .filter(
        method -> method.annotations()
          .exists(
            anno -> anno instanceof NamedAnnotation || anno instanceof InjectAnnotation
          )
      )
      .map(
        method -> ImmutableVariableImpl.builder()
          .name(method.name())
          .type(method.returnType())
          .build()
      );
    return fields()
      .filter(shouldBeInjected(assistAllInjectedFields()))
      .appendAll(injectedByMethods);
  }

  private Predicate<Variable> shouldBeInjected(boolean detectNamedFields) {
    return variable -> variable.annotations()
      .exists(
        annotation -> detectNamedFields ? annotation instanceof NamedAnnotation
          : annotation instanceof EnrichAnnotation
      );
  }
}
