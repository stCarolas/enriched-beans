package com.github.stcarolas.enrichedbeans.assistedinject.method;

import com.github.stcarolas.enrichedbeans.javamodel.method.Method;
import com.github.stcarolas.enrichedbeans.javamodel.variable.Variable;
import com.squareup.javapoet.CodeBlock;
import org.immutables.value.Value.Immutable;
import io.vavr.collection.Seq;

@Immutable
public abstract class FactoryMethodUsingConstructor extends Method {

  abstract Seq<Variable> injectedFields();

  @Override
  protected CodeBlock code() {
    Seq<String> fields = parameters().appendAll(injectedFields()).map(Variable::accessor);

    return CodeBlock.builder()
      .add("return new %s(%s);", returnType().toString(), fields.mkString(","))
      .build();
  }
}
