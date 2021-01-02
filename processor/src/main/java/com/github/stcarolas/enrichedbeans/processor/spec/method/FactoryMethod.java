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

@Immutable public abstract class FactoryMethod implements Method {
  abstract String name();
  abstract Seq<Variable> signatureFields();
  abstract Seq<Variable> objectFields();
  abstract TypeName returnType();

  static final Logger log = LogManager.getLogger();

  @Override
	public MethodSpec spec() {
    Seq<Variable> instanceFields = signatureFields().appendAll(objectFields());
      MethodSpec.Builder method = MethodSpec.methodBuilder(name())
          .returns(returnType())
          .addModifiers(Modifier.PUBLIC)
          .addCode(
              returnLine(
                  String.format("new %s(%s)",returnType().toString(),instanceFields.map(field -> field.accessor()).mkString(","))
              )
          );
      return signatureFields().foldLeft(method, withParameters).build();
	}

  Function2<MethodSpec.Builder, Variable, MethodSpec.Builder> withParameters = 
    (method, field) -> method.addParameter(
        field.asParameterSpec()
    );

  private String returnLine(String codeLine) {
      return "return " + codeLine + ";";
  }

  private String returnLine(Seq<String> fields, TypeName returnType) {
      return fields.mkString("return new " + returnType.toString() + "(", ",", ");");
  }

}
