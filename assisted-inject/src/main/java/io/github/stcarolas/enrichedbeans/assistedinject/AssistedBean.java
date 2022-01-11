package io.github.stcarolas.enrichedbeans.assistedinject;

import static io.vavr.API.Seq;
import static io.vavr.API.Success;

import java.util.function.Predicate;

import javax.lang.model.element.Modifier;

import io.github.stcarolas.enrichedbeans.annotations.Enrich;
import io.github.stcarolas.enrichedbeans.assistedinject.annotation.assisted.AssistedAnnotation;
import io.github.stcarolas.enrichedbeans.assistedinject.annotation.assisted.ImmutableAssistedAnnotation;
import io.github.stcarolas.enrichedbeans.assistedinject.annotation.enrich.EnrichAnnotation;
import io.github.stcarolas.enrichedbeans.assistedinject.annotation.inject.InjectAnnotation;
import io.github.stcarolas.enrichedbeans.assistedinject.annotation.named.NamedAnnotation;
import io.github.stcarolas.enrichedbeans.assistedinject.method.ImmutableFactoryMethodUsingConstructor;
import io.github.stcarolas.enrichedbeans.javamodel.bean.EnrichableBean;
import io.github.stcarolas.enrichedbeans.javamodel.bean.GeneratedBean;
import io.github.stcarolas.enrichedbeans.javamodel.method.Method;
import io.github.stcarolas.enrichedbeans.javamodel.method.constructor.Constructor;
import io.github.stcarolas.enrichedbeans.javamodel.method.constructor.ImmutableConstructImpl;
import io.github.stcarolas.enrichedbeans.javamodel.variable.ImmutableVariableImpl;
import io.github.stcarolas.enrichedbeans.javamodel.variable.Variable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.immutables.value.Value.Immutable;
import org.immutables.vavr.encodings.VavrEncodingEnabled;

import io.vavr.collection.Seq;
import io.vavr.control.Try;

public abstract class AssistedBean extends EnrichableBean {
  private static final Logger log = LogManager.getLogger();
  private static final String DEFAULT_FACTORY_METHOD_NAME = "from";

  @Override
  public Try<Seq<GeneratedBean>> process() {
    Modifier visibility = env().getOption("factoryVisibility").exists("package"::equals)
      ? Modifier.DEFAULT
      : Modifier.PUBLIC;
    String factoryClassNameSuffix = env()
      .getOption("factoryClassNameSuffix")
      .getOrElse("Factory");
    String factoryMethodName = env()
      .getOption("factoryMethodName")
      .getOrElse(DEFAULT_FACTORY_METHOD_NAME);
    return Success(
      Seq(
        ImmutableAssistingFactoryBean.builder()
          .packageName(packageName())
          .env(env())
          .className(className() + factoryClassNameSuffix)
          .fields(injectedFields())
          .visibility(visibility)
          .factoryMethod(createFactoryMethod(factoryMethodName))
          .constructor(constructorForFactory())
          .build()
      )
    );
  }

  private Constructor constructorForFactory() {
    return ImmutableConstructImpl.builder()
      .parameters(injectedFields())
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

  private Method createFactoryMethod(String factoryMethodName) {
    log.debug("Construct factory method using constructor with");
    return ImmutableFactoryMethodUsingConstructor.builder()
      .name(factoryMethodName)
      .returnType(type())
      .parameters(notInjectedFields())
      .injectedFields(injectedFields())
      .build();
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
      .appendAll(injectedByMethods)
      .map(
        field -> field.removeAnnotation(
          Enrich.class.getPackageName(),
          Enrich.class.getSimpleName()
        )
      );
  }

  private Predicate<Variable> shouldBeInjected(boolean detectNamedFields) {
    return variable -> variable.annotations()
      .exists(
        annotation -> detectNamedFields ? annotation instanceof NamedAnnotation
          : annotation instanceof EnrichAnnotation
      );
  }

  @Immutable
  @VavrEncodingEnabled
  public abstract static class AssistedBeanImpl extends AssistedBean {}
}
