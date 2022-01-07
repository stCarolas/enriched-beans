package com.github.stcarolas.enrichedbeans.assistedinject.method;

import com.github.stcarolas.enrichedbeans.assistedinject.BeanBuilder;
import com.github.stcarolas.enrichedbeans.javamodel.method.Method;
import com.github.stcarolas.enrichedbeans.javamodel.variable.Variable;
import com.squareup.javapoet.CodeBlock;
import org.immutables.value.Value.Immutable;
import org.immutables.vavr.encodings.VavrEncodingEnabled;

import io.vavr.collection.Seq;

@Immutable
@VavrEncodingEnabled
public abstract class FactoryMethodUsingBuilder extends Method {

  abstract BeanBuilder beanBuilder();
  abstract Seq<Variable> injectedFields();

  @Override
  protected CodeBlock code() {
    String settingsParametersLine = injectedFields()
      .foldLeft(
        new StringBuilder(),
        (builder, parameter) -> builder.append(
          String.format(".%s(%s)", parameter.name(), parameter.accessor())
        )
      )
      .toString();

    return CodeBlock.builder()
      .add(String.format("return %s(%s);", beanBuilder().build(), settingsParametersLine))
      .build();
  }
}
