package com.github.stcarolas.enrichedbeans.processor.java.annotation.assisted;

import com.github.stcarolas.enrichedbeans.processor.java.annotation.Annotation;

import org.immutables.value.Value.Immutable;
import org.immutables.vavr.encodings.VavrEncodingEnabled;

@Immutable
@VavrEncodingEnabled
public abstract class AssistedAnnotation extends Annotation {

  public boolean assistAllInjectedFields() {
    return parameters()
      .get("assistAllInjectedFields()")
      .map(it -> (Boolean) it)
      .getOrElse(false);
  }

  public boolean useBuilder() {
    return parameters()
      .get("useBuilder()")
      .map(it -> (Boolean) it)
      .getOrElse(false);
  }
}
