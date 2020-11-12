package com.github.stcarolas.enrichedbeans.processor.functions;

import javax.lang.model.element.Modifier;
import com.github.stcarolas.enrichedbeans.processor.Field;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import org.immutables.value.Value;
import io.vavr.Function2;
import io.vavr.Function4;
import io.vavr.collection.Seq;

@Value.Immutable(singleton = true, builder = false)
@Value.Style(instance="*")
public abstract class CreateFactoryMethod
    implements Function4<String, Seq<Field>, Seq<Field>, TypeName, MethodSpec> {

    @Override
    public MethodSpec apply(
        String name,
        Seq<Field> signatureFields,
        Seq<Field> objectFields,
        TypeName returnType
    ) {
        MethodSpec.Builder method = MethodSpec.methodBuilder(name)
            .returns(returnType)
            .addModifiers(Modifier.PUBLIC)
            .addCode(
                returnLine(
                    signatureFields.map(Field::name)
                        .appendAll(
                            objectFields.map(field -> field.name())
                                .map(fieldName -> fieldName + ".get()")
                        ),
                    returnType
                )
            );
        return signatureFields.foldLeft(method, withParameters).build();
    }

    Function2<MethodSpec.Builder, Field, MethodSpec.Builder> withParameters = (method, field) -> method.addParameter(
        field.asParameterSpec()
    );

    private String returnLine(Seq<String> fields, TypeName returnType) {
        return fields.mkString("return new " + returnType.toString() + "(", ",", ");");
    }
}
