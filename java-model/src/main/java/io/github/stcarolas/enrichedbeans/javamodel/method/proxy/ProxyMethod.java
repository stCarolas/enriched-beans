package  io.github.stcarolas.enrichedbeans.javamodel.method.proxy;

import io.github.stcarolas.enrichedbeans.javamodel.annotation.Annotation;
import io.github.stcarolas.enrichedbeans.javamodel.method.Method;
import io.github.stcarolas.enrichedbeans.javamodel.variable.Variable;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.TypeName;

import org.immutables.value.Value.Derived;
import org.immutables.value.Value.Immutable;

import io.vavr.collection.Seq;

@Immutable
public abstract class ProxyMethod extends Method{

  abstract public String name();
  abstract Method method();

  @Derived
  public Seq<Variable> parameters(){
    return method().parameters();
  }

  @Derived
  public Seq<Annotation> annotations(){
    return method().annotations();
  }

  @Derived
  public TypeName returnType(){
    return method().returnType();
  }

  @Override
  public CodeBlock code() {
    return CodeBlock.builder()
      .addStatement(
        String.format(
          "return this.%s(%s)",
          method().name(),
          method().parameters().map(Variable::name).mkString(",")
        )
      )
      .build();
  }

}
