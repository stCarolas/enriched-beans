package com.github.stcarolas.enrichedbeans.processor;

import static com.squareup.javapoet.TypeSpec.classBuilder;
import java.util.function.Function;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.lang.model.element.Modifier;
import com.github.stcarolas.enrichedbeans.processor.functions.CreateFactoryMethod;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeSpec.Builder;
import com.squareup.javapoet.ClassName;
import org.immutables.value.Value;
import io.vavr.Function2;
import io.vavr.Function4;
import io.vavr.collection.Seq;

import static com.github.stcarolas.enrichedbeans.processor.functions.ImmutableCreateConstructor.*;
import static com.github.stcarolas.enrichedbeans.processor.functions.ImmutableCreateFactoryMethod.*;

@Value.Immutable
public interface AssistingFactorySpec {
    String packageName();
    String factoryClassName();
    String factoryMethodName();
    TypeName targetType();
    Seq<Field> injectingFields();
    Seq<Field> instanceFields();
    FactoryVisibility visibility();
    boolean defineAsVavrFunctionInterface();

    default public TypeSpec create() {
        Builder factory = classBuilder(factoryClassName())
            .addAnnotation(Singleton.class)
            .addAnnotation(
                AnnotationSpec.builder(Named.class)
                    .addMember("value", String.format("\"%s\"", factoryClassName()))
                    .build()
            )
            .addFields(injectingFields().map(toPrivateFactoryField))
            .addMethod(CreateConstructor().apply(injectingFields()))
            .addMethod(
                CreateFactoryMethod().apply(
                    factoryMethodName(),
                    instanceFields(),
                    injectingFields(),
                    targetType()
                )
            );
        if (visibility() == FactoryVisibility.PUBLIC) {
            factory.addModifiers(Modifier.PUBLIC);
        }
        return factory.build();
    }

    Function<Field, ParameterizedTypeName> provider = field -> ParameterizedTypeName.get(
        ClassName.get("javax.inject", "Provider"),
        field.typeName()
    );

    Function<Field, FieldSpec> toPrivateFactoryField = field -> FieldSpec.builder(
        provider.apply(field),
        field.name(),
        Modifier.PRIVATE
    )
        .build();

}
