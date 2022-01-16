package io.github.stcarolas.enrichedbeans.assistedinject.method;

import io.github.stcarolas.enrichedbeans.javamodel.method.Method;
import io.github.stcarolas.enrichedbeans.javamodel.variable.Variable;
import com.squareup.javapoet.CodeBlock;
import org.immutables.value.Value.Immutable;
import org.immutables.vavr.encodings.VavrEncodingEnabled;

import io.vavr.collection.Seq;

@Immutable
@VavrEncodingEnabled
public abstract class FactoryMethodUsingConstructor extends Method {

  abstract Seq<Variable> injectedFields();

  @Override
  protected CodeBlock code() {
    Seq<String> fields = parameters().appendAll(injectedFields()).map(Variable::name);

    return CodeBlock.builder()
      .add(String.format("return new %s(%s);", returnType().toString(), fields.mkString(",")))
      .build();
  }
}
