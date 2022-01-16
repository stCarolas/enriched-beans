package io.github.stcarolas.enrichedbeans.assistedinject;

import static io.vavr.API.Seq;
import static io.vavr.API.Success;
import java.util.function.Predicate;
import javax.lang.model.element.Modifier;
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
  public Try<Seq<GeneratedBean>> enrich() {
    return Success(Seq(factoryBean()));
  }

  protected AssistingFactoryBean factoryBean() {
    return ImmutableAssistingFactoryBean.builder()
      .packageName(packageName())
      .env(env())
      .className(factoryClassName())
      .fields(injectedFields())
      .visibility(visibility())
      .factoryMethod(createFactoryMethod())
      .constructor(constructorForFactory())
      .build();
  }

  protected String factoryMethodName() {
    return env().getOption("factoryMethodName").getOrElse(DEFAULT_FACTORY_METHOD_NAME);
  }

  protected String factoryClassName() {
    return className() + factoryClassNameSuffix();
  }

  protected String factoryClassNameSuffix() {
    return env().getOption("factoryClassNameSuffix").getOrElse("Factory");
  }

  protected Modifier visibility() {
    return env().getOption("factoryVisibility").exists("package"::equals)
      ? Modifier.DEFAULT
      : Modifier.PUBLIC;
  }

  protected Constructor constructorForFactory() {
    return ImmutableConstructImpl.builder()
      .parameters(injectedFields())
      .annotations(
        Seq(
          ImmutableAnnotationImpl.builder()
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

  protected Method createFactoryMethod() {
    log.debug("Construct factory method using constructor with");
    return ImmutableFactoryMethodUsingConstructor.builder()
      .name(factoryMethodName())
      .returnType(type())
      .parameters(notInjectedFields())
      .injectedFields(injectedFields())
      .build();
  }

  protected Seq<Variable> notInjectedFields() {
    return fields().reject(shouldBeInjected(assistAllInjectedFields()));
  }

  protected Seq<Variable> injectedFields() {
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
