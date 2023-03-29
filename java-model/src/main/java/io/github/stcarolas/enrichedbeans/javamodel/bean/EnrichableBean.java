package io.github.stcarolas.enrichedbeans.javamodel.bean;

import org.immutables.value.Value.Immutable;
import org.immutables.vavr.encodings.VavrEncodingEnabled;
import io.vavr.collection.Seq;
import io.vavr.control.Try;
import static io.vavr.API.*;

public abstract class EnrichableBean extends ProcessedBean {

  abstract public Try<Seq<GeneratedBean>> enrich();

  @Immutable
  @VavrEncodingEnabled
  public abstract static class EnrichableBeanImpl extends EnrichableBean {

    @Override
    public Try<Seq<GeneratedBean>> enrich() {
      return Success(Seq());
    }
  }

}
