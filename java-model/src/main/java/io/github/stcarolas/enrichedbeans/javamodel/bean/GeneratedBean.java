package io.github.stcarolas.enrichedbeans.javamodel.bean;

import com.squareup.javapoet.TypeSpec;
import org.immutables.value.Value.Immutable;
import org.immutables.vavr.encodings.VavrEncodingEnabled;
import io.vavr.control.Try;

public abstract class GeneratedBean extends Bean {

  abstract protected TypeSpec spec();

  public Try<GeneratedBean> write() {
    return env().writeSource(packageName(), spec()).map(it -> this);
  }

  @Immutable
  @VavrEncodingEnabled
  public abstract static class GeneratedBeanImpl extends GeneratedBean {}
}
