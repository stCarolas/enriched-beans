package io.github.stcarolas.enrichedbeans.immutablescriteriaspring.bean;

import static io.vavr.API.Seq;
import static io.vavr.API.Success;

import org.immutables.value.Value.Immutable;
import org.immutables.vavr.encodings.VavrEncodingEnabled;

import io.github.stcarolas.enrichedbeans.javamodel.bean.EnrichableBean;
import io.github.stcarolas.enrichedbeans.javamodel.bean.GeneratedBean;
import io.vavr.collection.Seq;
import io.vavr.control.Try;

public abstract class CriteriaRepositoryBean extends EnrichableBean {

  @Override
  public Try<Seq<GeneratedBean>> enrich() {
    return Success(Seq(
      ImmutableRepositoryConfigurationBean.builder()
        .entityName(className())
        .packageName(packageName())
        .build()
    ));
  }

  @Immutable
  @VavrEncodingEnabled
  public abstract static class CriteriaRepositoryBeanImpl extends CriteriaRepositoryBean {}

}
