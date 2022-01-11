package  io.github.stcarolas.enrichedbeans.javamodel.method;

import javax.lang.model.element.Modifier;

import io.github.stcarolas.enrichedbeans.javamodel.annotation.Annotation;
import io.github.stcarolas.enrichedbeans.javamodel.variable.Variable;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;

import org.immutables.value.Value.Immutable;
import org.immutables.vavr.encodings.VavrEncodingEnabled;

import io.vavr.collection.Seq;

public abstract class Method {
  abstract public String name();
  abstract public Seq<Variable> parameters();
  abstract public Seq<Annotation> annotations();
  abstract public TypeName returnType();

  public MethodSpec spec(){
    return MethodSpec.methodBuilder(name())
      .addParameters(parameters().map(Variable::asParameterSpec))
      .addAnnotations(annotations().map(Annotation::spec))
      .addModifiers(Modifier.PUBLIC)
      .addCode(code())
      .returns(returnType())
      .build();
  }

  protected CodeBlock code(){
    return CodeBlock.builder()
      .add("return null;")
      .build();
  }

  @Immutable
  @VavrEncodingEnabled
  public abstract static class MethodImpl extends Method {}

}
