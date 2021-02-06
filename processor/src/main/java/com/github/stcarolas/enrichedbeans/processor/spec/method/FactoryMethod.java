package  com.github.stcarolas.enrichedbeans.processor.spec.method;

import javax.lang.model.element.Modifier;

import com.github.stcarolas.enrichedbeans.processor.java.Variable;
import com.github.stcarolas.enrichedbeans.processor.spec.assistingfactory.CallBuilder;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.immutables.value.Value.Immutable;

import io.vavr.Function2;
import io.vavr.collection.Seq;
import io.vavr.control.Option;

@Immutable public abstract class FactoryMethod
  implements Method {

  abstract Seq<Variable> objectFields();

  @Override public MethodSpec spec() {
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
