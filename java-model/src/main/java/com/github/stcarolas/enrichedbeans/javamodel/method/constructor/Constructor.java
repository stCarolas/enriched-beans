package com.github.stcarolas.enrichedbeans.javamodel.method.constructor;

import static org.immutables.value.Value.Immutable;
import com.github.stcarolas.enrichedbeans.javamodel.method.Method;
import com.squareup.javapoet.CodeBlock;
import org.immutables.vavr.encodings.VavrEncodingEnabled;

public abstract class Constructor extends Method {

  @Override
  protected CodeBlock code() {
    return parameters()
      .foldLeft(
        CodeBlock.builder(),
        (builder, field) -> builder.add("this.$N = $N;", field.name(), field.name())
      )
      .build();
  }

  @Immutable
  @VavrEncodingEnabled
  public abstract static class ConstructImpl extends Constructor {}
}
