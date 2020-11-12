package com.github.stcarolas.enrichedbeans.processor.functions;

import java.util.function.Function;
import javax.inject.Inject;
import javax.lang.model.element.Modifier;
import com.github.stcarolas.enrichedbeans.processor.Field;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import org.immutables.value.Value;
import io.vavr.Function2;
import io.vavr.collection.Seq;

@Value.Style(instance = "*")
@Value.Immutable(singleton=true, builder=false) 
public abstract class CreateConstructor implements Function<Seq<Field>, MethodSpec> {

    @Override
    public MethodSpec apply(Seq<Field> fields) {
        return fields
            .map(field -> field.withType(provider.apply(field)))
            .foldLeft(
                MethodSpec.constructorBuilder()
                    .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(Inject.class),
                addParameterWithAssignment
            )
            .build();
    }

    Function<Field, ParameterizedTypeName> provider = field -> ParameterizedTypeName.get(
        ClassName.get("javax.inject", "Provider"),
        field.typeName()
    );

    Function2<MethodSpec.Builder, Field, MethodSpec.Builder> addParameter = (method, field) -> method.addParameter(
        field.asParameterSpec()
    );

    Function2<MethodSpec.Builder, Field, MethodSpec.Builder> addParameterWithAssignment = (method, field) -> addParameter.apply(
        method,
        field
    )
        .addStatement("this.$N = $N", field.name(), field.name());
}
