package  com.github.stcarolas.enrichedbeans.processor.spec.method;

import javax.lang.model.element.Modifier;

import com.github.stcarolas.enrichedbeans.processor.java.Variable;
import com.github.stcarolas.enrichedbeans.processor.spec.method.api.MethodWithSpec;
import com.squareup.javapoet.MethodSpec;
import org.immutables.value.Value.Immutable;

import io.vavr.collection.Seq;

@Immutable public abstract class FactoryMethod
  implements MethodWithSpec {

  abstract Seq<Variable> objectFields();

  public MethodSpec spec() {
    MethodSpec.Builder method = MethodSpec.methodBuilder(name())
      .returns(returnType())
      .addModifiers(Modifier.PUBLIC)
      .addCode(returnNewInstance());

    method = parameters().map(Variable::asParameterSpec)
      .foldLeft(method, MethodSpec.Builder::addParameter);

    return method.build();
	}

  private String returnNewInstance() {
    Seq<String> fields = parameters().appendAll(objectFields())
      .map(Variable::accessor);

    return String.format(
      "return new %s(%s);",
      returnType().toString(),
      fields.mkString(",")
    );
  }

}
