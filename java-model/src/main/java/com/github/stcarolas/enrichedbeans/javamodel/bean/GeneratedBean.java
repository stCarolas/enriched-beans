package com.github.stcarolas.enrichedbeans.javamodel.bean;

import com.squareup.javapoet.TypeSpec;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.immutables.value.Value.Immutable;
import org.immutables.vavr.encodings.VavrEncodingEnabled;
import io.vavr.control.Try;

public abstract class GeneratedBean extends Bean {
  private static final Logger log = LogManager.getLogger();

  abstract protected TypeSpec spec();

  public Try<GeneratedBean> write() {
    return env().writeSource(packageName(), spec()).map(it -> this);
  }

  @Immutable
  @VavrEncodingEnabled
  public abstract static class GeneratedBeanImpl extends GeneratedBean {}
}
