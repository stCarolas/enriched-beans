package io.github.stcarolas.enrichedbeans.javamodel.method.constructor;

import static org.immutables.value.Value.Immutable;

import javax.lang.model.element.Modifier;

import io.github.stcarolas.enrichedbeans.javamodel.annotation.Annotation;
import io.github.stcarolas.enrichedbeans.javamodel.method.Method;
import io.github.stcarolas.enrichedbeans.javamodel.variable.Variable;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;

import org.immutables.vavr.encodings.VavrEncodingEnabled;

import io.vavr.NotImplementedError;

public abstract class Constructor extends Method {

  public String name(){
    return "";
  }

  public TypeName returnType(){
    throw new NotImplementedError();
  }

  @Override
  public MethodSpec spec(){
    return MethodSpec.constructorBuilder()
      .addParameters(parameters().map(Variable::asParameterSpec))
      .addAnnotations(annotations().map(Annotation::spec))
      .addModifiers(Modifier.PUBLIC)
      .addCode(code())
      .build();
  }

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
