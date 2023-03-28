package io.github.stcarolas.enrichedbeans.javamodel.method;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import io.github.stcarolas.enrichedbeans.javamodel.annotation.Annotation;
import io.github.stcarolas.enrichedbeans.javamodel.variable.Variable;
import io.vavr.collection.Seq;
import javax.lang.model.element.Modifier;
import org.immutables.value.Value.Immutable;
import org.immutables.vavr.encodings.VavrEncodingEnabled;

public abstract class Method {

  public abstract String name();

  public abstract Seq<Variable> parameters();

  public abstract Seq<Annotation> annotations();

  public abstract TypeName returnType();

  public MethodSpec spec() {
    return MethodSpec
      .methodBuilder(name())
      .addParameters(parameters().map(Variable::asParameterSpec))
      .addAnnotations(annotations().map(Annotation::spec))
      .addModifiers(Modifier.PUBLIC)
      .addCode(code())
      .returns(returnType())
      .build();
  }

  public abstract CodeBlock code();

  @Immutable
  @VavrEncodingEnabled
  public abstract static class MethodImpl extends Method {}
}
