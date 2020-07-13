package com.github.stcarolas.enrichedbeans.processor;

import com.squareup.javapoet.TypeName;
import org.immutables.value.Value;
import io.vavr.collection.Seq;
import lombok.RequiredArgsConstructor;

@Value.Immutable
public interface AssistingFactoryConfig {
    String factoryClassName();
    String factoryMethodName();
    TypeName targetType();
    Seq<Field> injectingFields();
    Seq<Field> instanceFields();
    FactoryVisibility visibility();
}
