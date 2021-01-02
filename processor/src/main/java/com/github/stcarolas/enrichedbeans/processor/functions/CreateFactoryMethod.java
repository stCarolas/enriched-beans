package com.github.stcarolas.enrichedbeans.processor.functions;

import javax.lang.model.element.Modifier;

import com.github.stcarolas.enrichedbeans.processor.java.Variable;
import com.github.stcarolas.enrichedbeans.processor.spec.assistingfactory.CallBuilder;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.immutables.value.Value;

import io.vavr.Function2;
import io.vavr.Function5;
import io.vavr.collection.Seq;
import io.vavr.control.Option;

@Value.Immutable(singleton = true, builder = false)
@Value.Style(instance="*")
public abstract class CreateFactoryMethod
    implements Function5<String, Seq<Variable>, Seq<Variable>, TypeName, Option<String>, MethodSpec> {
    CallBuilder callBuilder = new CallBuilder();

    static final Logger log = LogManager.getLogger();

    @Override
    public MethodSpec apply(
        String name,
        Seq<Variable> signatureFields,
        Seq<Variable> objectFields,
        TypeName returnType,
        Option<String> builderForFactoryMethod
    ) {
      log.info("builder: {}",builderForFactoryMethod);
      Seq<Variable> instanceFields = signatureFields.appendAll(objectFields);
        MethodSpec.Builder method = MethodSpec.methodBuilder(name)
            .returns(returnType)
            .addModifiers(Modifier.PUBLIC)
            .addCode(
                returnLine(
                  builderForFactoryMethod.isEmpty()
                    ? String.format("new %s(%s)",returnType.toString(),instanceFields.map(field -> field.accessor()).mkString(","))
                    : callBuilder.codeLine(builderForFactoryMethod.get(), instanceFields)
                )
            );
        return signatureFields.foldLeft(method, withParameters).build();
    }

    Function2<MethodSpec.Builder, Variable, MethodSpec.Builder> withParameters = (method, field) -> method.addParameter(
        field.asParameterSpec()
    );

    private String returnLine(String codeLine) {
        return "return " + codeLine + ";";
    }

    private String returnLine(Seq<String> fields, TypeName returnType) {
        return fields.mkString("return new " + returnType.toString() + "(", ",", ");");
    }
}
