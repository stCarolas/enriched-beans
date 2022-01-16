package io.github.stcarolas.enrichedbeans.immutablesassistedinject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.immutables.value.Value.Immutable;
import org.immutables.vavr.encodings.VavrEncodingEnabled;
import io.github.stcarolas.enrichedbeans.annotations.Enrich;
import io.github.stcarolas.enrichedbeans.assistedinject.AssistedBean;
import io.github.stcarolas.enrichedbeans.assistedinject.annotation.enrich.EnrichAnnotation;
import io.github.stcarolas.enrichedbeans.assistedinject.annotation.inject.InjectAnnotation;
import io.github.stcarolas.enrichedbeans.assistedinject.annotation.named.NamedAnnotation;
import io.github.stcarolas.enrichedbeans.javamodel.method.Method;
import io.github.stcarolas.enrichedbeans.javamodel.variable.ImmutableVariableImpl;
import io.github.stcarolas.enrichedbeans.javamodel.variable.Variable;
import io.vavr.collection.Seq;

public abstract class AssistedImmutablesBean extends AssistedBean {
  private static final Logger log = LogManager.getLogger();

  protected Seq<Variable> injectedFields() {
    return methods()
      .filter(
        method -> method.annotations()
          .exists(
            anno -> anno instanceof NamedAnnotation
              || anno instanceof EnrichAnnotation
              || anno instanceof InjectAnnotation
          )
      )
      .map(
        method -> ImmutableVariableImpl.builder()
          .name(method.name())
          .type(method.returnType())
          .build()
          .removeAnnotation(Enrich.class.getPackageName(), Enrich.class.getSimpleName())
      );
  }

  protected Method createFactoryMethod() {
    log.debug("Construct factory method using constructor with");
    return ImmutableFactoryMethodUsingBuilder.builder()
      .name(factoryMethodName())
      .injectedFields(injectedFields())
      .packageName(packageName())
      .className(className())
      .build();
  }

  @Immutable
  @VavrEncodingEnabled
  public abstract static class AssistedImmutablesBeanImpl
    extends AssistedImmutablesBean {}
}
