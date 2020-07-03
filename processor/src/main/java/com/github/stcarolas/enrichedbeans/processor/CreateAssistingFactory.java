package com.github.stcarolas.enrichedbeans.processor;

import javax.annotation.processing.ProcessingEnvironment;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.lang.model.element.Modifier;
import static com.squareup.javapoet.TypeSpec.classBuilder;
import java.util.function.Function;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import io.vavr.Function2;
import io.vavr.collection.Seq;
import lombok.RequiredArgsConstructor;

public class CreateAssistingFactory {

    public TypeSpec apply(
        String factoryName,
        TypeName targetType,
        Seq<Field> injectingFields,
        Seq<Field> instanceFields,
        AssistingFactoryConfig config
    ) {
        return classBuilder(factoryName)
            .addAnnotation(Singleton.class)
            .addAnnotation(Named.class)
            .addModifiers(Modifier.PUBLIC)
            .addFields(injectingFields.map(privateFactoryField))
            .addMethod(constructor(injectingFields))
            .addMethod(factoryMethod(instanceFields, injectingFields, targetType))
            .build();
    }

    private Function<Field, FieldSpec> privateFactoryField = field -> FieldSpec.builder(
        field.typeName(),
        field.name(),
        Modifier.PRIVATE
    )
        .build();

    private MethodSpec constructor(Seq<Field> fields) {
        return fields.foldLeft(
            MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Inject.class)
                .build(),
            addParameterWithAssignment
        );
    }

    private Function2<MethodSpec, Field, MethodSpec> addParameter = (method, field) -> method.toBuilder()
        .addParameter(field.asParameterSpec())
        .build();

    private Function2<MethodSpec, Field, MethodSpec> addParameterWithAssignment = (method, field) -> addParameter.apply(
        method,
        field
    )
        .toBuilder()
        .addStatement("this.$N = $N", field.name(), field.name())
        .build();

    private MethodSpec factoryMethod(
        Seq<Field> signatureFields,
        Seq<Field> objectFields,
        TypeName returnType
    ) {
        return signatureFields.foldLeft(
            MethodSpec.methodBuilder("from")
                .returns(returnType)
                .addModifiers(Modifier.PUBLIC)
                .build(),
            addParameter
        )
            .toBuilder()
            .addCode(returnLine(signatureFields.appendAll(objectFields), returnType))
            .build();
    }

    private String returnLine(Seq<Field> fields, TypeName returnType) {
        return fields.map(Field::name)
            .mkString("return new " + returnType.toString() + "(", ",", ");");
    }
}
