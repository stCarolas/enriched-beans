package com.github.stcarolas.enrichedbeans.processor.spec.method;

import com.github.stcarolas.enrichedbeans.processor.java.Variable;
import com.squareup.javapoet.TypeName;

import org.immutables.value.Value.Immutable;

import io.vavr.collection.Seq;

@Immutable public interface Method extends HasMethodSpec {
  String name();
  Seq<Variable> parameters();
  TypeName returnType();
}
