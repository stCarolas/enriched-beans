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
import com.squareup.javapoet.TypeSpec.Builder;
import io.vavr.Function2;
import io.vavr.collection.Seq;
import lombok.RequiredArgsConstructor;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.ClassName;

public class CreateAssistingFactory {

    public TypeSpec apply(AssistingFactoryConfig config) {
        Builder factory = classBuilder(config.factoryClassName())
            .addAnnotation(Singleton.class)
            .addAnnotation(Named.class)
            .addFields(config.injectingFields().map(privateFactoryField))
            .addMethod(constructor(config.injectingFields()))
            .addMethod(
                factoryMethod(
                    config.factoryMethodName(),
                    config.instanceFields(),
                    config.injectingFields(),
                    config.targetType()
                )
            );
        if (config.visibility() == FactoryVisibility.PUBLIC) {
            factory.addModifiers(Modifier.PUBLIC);
        }
        return factory.build();
    }

    private Function<Field, ParameterizedTypeName> provider = field -> ParameterizedTypeName.get(
        ClassName.get("javax.inject", "Provider"),
        field.typeName()
    );

    private Function<Field, FieldSpec> privateFactoryField = field -> FieldSpec.builder(
        provider.apply(field),
        field.name(),
        Modifier.PRIVATE
    )
        .build();

    private MethodSpec constructor(Seq<Field> fields) {
        return fields.map(field -> field.withType(provider.apply(field)))
            .foldLeft(
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
        String name,
        Seq<Field> signatureFields,
        Seq<Field> objectFields,
        TypeName returnType
    ) {
        return signatureFields.foldLeft(
            MethodSpec.methodBuilder(name)
                .returns(returnType)
                .addModifiers(Modifier.PUBLIC)
                .build(),
            addParameter
        )
            .toBuilder()
            .addCode(
                returnLine(
                    signatureFields.map(Field::name)
                        .appendAll(
                            objectFields.map(field -> field.name())
                                .map(fieldName -> fieldName + ".get()")
                        ),
                    returnType
                )
            )
            .build();
    }

    private String returnLine(Seq<String> fields, TypeName returnType) {
        return fields.mkString("return new " + returnType.toString() + "(", ",", ");");
    }
}
