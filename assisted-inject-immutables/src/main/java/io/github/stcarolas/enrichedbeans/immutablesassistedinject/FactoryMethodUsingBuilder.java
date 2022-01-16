package io.github.stcarolas.enrichedbeans.immutablesassistedinject;

import static io.vavr.API.Seq;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.TypeName;
import org.immutables.value.Value.Immutable;
import org.immutables.vavr.encodings.VavrEncodingEnabled;
import io.github.stcarolas.enrichedbeans.javamodel.method.Method;
import io.github.stcarolas.enrichedbeans.javamodel.variable.Variable;
import io.vavr.collection.Seq;

@Immutable
@VavrEncodingEnabled
public abstract class FactoryMethodUsingBuilder extends Method {

  abstract String className();

  abstract String packageName();

  abstract Seq<Variable> injectedFields();

  public Seq<Variable> parameters() {
    return Seq();
  }

  public TypeName returnType() {
    return ClassName.get(String.format("%s.Immutable%s", packageName(), className()), "Builder");
  }

  @Override
  protected CodeBlock code() {
    String builderParametersLine = injectedFields()
      .foldLeft(
        new StringBuilder(),
        (builder, parameter) -> builder.append(
          String.format(".%s(%s)", parameter.name(), parameter.name())
        )
      )
      .toString();

    return CodeBlock.builder()
      .add(
        String.format(
          "return %s.Immutable%s.builder()%s;",
          packageName(), className(),
          builderParametersLine
        )
      )
      .build();
  }
}
