package  com.github.stcarolas.enrichedbeans.processor.spec.method;

import javax.lang.model.element.Modifier;

import com.github.stcarolas.enrichedbeans.processor.java.Variable;
import com.github.stcarolas.enrichedbeans.processor.spec.HasName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.immutables.value.Value.Derived;
import org.immutables.value.Value.Immutable;

import io.vavr.collection.Seq;

@Immutable public abstract class ProxyMethod
  implements HasMethodSpec, HasName {

  abstract Method method();

  @Override
	public MethodSpec spec() {
    return MethodSpec.methodBuilder(name())
      .addParameters(method().parameters().map(Variable::asParameterSpec).toJavaList())
      .addCode(callMethod())
      .addModifiers(Modifier.PUBLIC)
      .returns(method().returnType())
      .build();
  }

  private CodeBlock callMethod() {
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
