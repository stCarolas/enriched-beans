package io.github.stcarolas.enrichedbeans.assistedinject.annotation.assisted;

import io.github.stcarolas.enrichedbeans.javamodel.annotation.Annotation;

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

}
