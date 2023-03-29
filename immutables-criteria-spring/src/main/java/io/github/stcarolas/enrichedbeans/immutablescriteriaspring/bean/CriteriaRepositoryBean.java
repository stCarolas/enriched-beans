package io.github.stcarolas.enrichedbeans.immutablescriteriaspring.bean;

import static io.vavr.API.Seq;
import static io.vavr.API.Success;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.immutables.value.Value.Immutable;
import org.immutables.vavr.encodings.VavrEncodingEnabled;

import io.github.stcarolas.enrichedbeans.javamodel.bean.EnrichableBean;
import io.github.stcarolas.enrichedbeans.javamodel.bean.GeneratedBean;
import io.vavr.collection.Seq;
import io.vavr.control.Try;

public abstract class CriteriaRepositoryBean extends EnrichableBean {

  private Logger log = LogManager.getLogger();

  @Override
  public Try<Seq<GeneratedBean>> enrich() {
    log.always().log("Enrich CriteriaRepositoryBean: " + packageName() + "." + className());
    return Success(Seq(
      ImmutableRepositoryConfigurationBean.builder()
        .entityName(className())
        .packageName(packageName())
        .env(env())
        .build()
    ));
  }

  @Immutable
  @VavrEncodingEnabled
  public abstract static class CriteriaRepositoryBeanImpl extends CriteriaRepositoryBean {}

}
