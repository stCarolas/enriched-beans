package io.github.stcarolas.enrichedbeans.assistedinject;

import static io.vavr.API.Seq;

import io.github.stcarolas.enrichedbeans.annotations.Enrich;
import io.github.stcarolas.enrichedbeans.assistedinject.annotation.assisted.AssistedAnnotation;
import io.github.stcarolas.enrichedbeans.assistedinject.annotation.enrich.EnrichAnnotation;
import io.github.stcarolas.enrichedbeans.assistedinject.annotation.inject.InjectAnnotation;
import io.github.stcarolas.enrichedbeans.assistedinject.annotation.named.NamedAnnotation;
import io.github.stcarolas.enrichedbeans.assistedinject.method.ImmutableFactoryMethodUsingConstructor;
import io.github.stcarolas.enrichedbeans.javamodel.annotation.ImmutableAnnotationImpl;
import io.github.stcarolas.enrichedbeans.javamodel.bean.EnrichableBean;
import io.github.stcarolas.enrichedbeans.javamodel.bean.GeneratedBean;
import io.github.stcarolas.enrichedbeans.javamodel.method.Method;
import io.github.stcarolas.enrichedbeans.javamodel.method.constructor.Constructor;
import io.github.stcarolas.enrichedbeans.javamodel.method.constructor.ImmutableConstructImpl;
import io.github.stcarolas.enrichedbeans.javamodel.variable.ImmutableVariableImpl;
import io.github.stcarolas.enrichedbeans.javamodel.variable.Variable;
import io.vavr.API;
import io.vavr.collection.Seq;
import io.vavr.control.Try;
import java.util.function.Predicate;
import javax.lang.model.element.Modifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.immutables.value.Value.Immutable;
import org.immutables.vavr.encodings.VavrEncodingEnabled;

public abstract class AssistedBean extends EnrichableBean {

  private static final Logger log = LogManager.getLogger();
  private static final String DEFAULT_FACTORY_METHOD_NAME = "from";
  private static final String DEFAULT_FACTORY_CLASS_NAME_SUFFIX = "Factory";

  @Override
  public Try<Seq<GeneratedBean>> enrich() {
    return factoryBean().map(API::Seq);
  }

  protected Try<AssistingFactoryBean> factoryBean() {
    return factoryMethod()
      .map(factoryMethod ->
        ImmutableAssistingFactoryBean
          .builder()
          .className(factoryClassName())
          .packageName(packageName())
          .env(env())
          .isAbstract(Boolean.FALSE)
          .fields(injectedFields())
          .visibility(visibility())
          .factoryMethod(factoryMethod)
          .constructor(constructorForFactory())
          .build()
      );
  }

  protected String factoryClassName() {
    return className() + factoryClassNameSuffix();
  }

  protected String factoryClassNameSuffix() {
    return env()
      .getOption("factoryClassNameSuffix")
      .getOrElse(DEFAULT_FACTORY_CLASS_NAME_SUFFIX);
  }

  protected Modifier visibility() {
    return env().getOption("factoryVisibility").exists("package"::equals)
      ? Modifier.DEFAULT
      : Modifier.PUBLIC;
  }

  protected Constructor constructorForFactory() {
    return ImmutableConstructImpl
      .builder()
      .parameters(injectedFields())
      .annotations(
        Seq(
          ImmutableAnnotationImpl
            .builder()
            .className("Inject")
            .packageName("javax.inject")
            .build()
        )
      )
      .build();
  }

  private boolean assistAllInjectedFields() {
    return annotations()
      .find(anno -> anno instanceof AssistedAnnotation)
      .map(anno -> (AssistedAnnotation) anno)
      .map(AssistedAnnotation::assistAllInjectedFields)
      .getOrElse(false);
  }

  protected Try<Method> factoryMethod() {
    log.debug("Construct factory method using constructor with");
    return typeName()
      .map(returnType ->
        ImmutableFactoryMethodUsingConstructor
          .builder()
          .name(factoryMethodName())
          .returnType(returnType)
          .parameters(notInjectedFields())
          .injectedFields(injectedFields())
          .build()
      );
  }

  protected String factoryMethodName() {
    return env()
      .getOption("factoryMethodName")
      .getOrElse(DEFAULT_FACTORY_METHOD_NAME);
  }

  protected Seq<Variable> notInjectedFields() {
    // TODO filter static fields and inited fields
    return fields()
      .reject(shouldBeInjected(assistAllInjectedFields()))
      .map(field -> field.removeAnnotation("com.fasterxml.jackson.annotation", "JsonIgnore"))
      .map(field -> field.removeAnnotation("org.springframework.data.annotation","Id"))
      .map(field -> field.removeAnnotation("org.springframework.data.annotation","Transient"))
    ;

  }

  protected Seq<Variable> injectedFields() {
    Seq<Variable> injectedByMethods = methods()
      .filter(method ->
        method
          .annotations()
          .exists(anno ->
            anno instanceof NamedAnnotation || anno instanceof InjectAnnotation
          )
      )
      .map(method ->
        ImmutableVariableImpl
          .builder()
          .name(method.name())
          .typeName(method.returnType())
          .build()
      );
    return fields()
      .filter(shouldBeInjected(assistAllInjectedFields()))
      .appendAll(injectedByMethods)
      .map(field ->
        field.removeAnnotation(
          Enrich.class.getPackageName(),
          Enrich.class.getSimpleName()
        )
      )
      .map(field -> field.removeAnnotation("com.fasterxml.jackson.annotation", "JsonIgnore"))
      .map(field -> field.removeAnnotation("org.springframework.data.annotation","Id"))
      .map(field -> field.removeAnnotation("org.springframework.data.annotation","Transient"))
    ;
  }

  private Predicate<Variable> shouldBeInjected(boolean detectNamedFields) {
    return variable ->
      variable
        .annotations()
        .exists(annotation ->
          detectNamedFields
            ? annotation instanceof NamedAnnotation
            : annotation instanceof EnrichAnnotation
        );
  }

  @Immutable
  @VavrEncodingEnabled
  public abstract static class AssistedBeanImpl extends AssistedBean {}
}
