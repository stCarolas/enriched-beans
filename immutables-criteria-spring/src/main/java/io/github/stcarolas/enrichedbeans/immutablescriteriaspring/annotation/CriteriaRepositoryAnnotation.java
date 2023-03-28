package io.github.stcarolas.enrichedbeans.immutablescriteriaspring.annotation;

import io.github.stcarolas.enrichedbeans.javamodel.annotation.Annotation;
import org.immutables.value.Value.Immutable;
import org.immutables.vavr.encodings.VavrEncodingEnabled;

@Immutable
@VavrEncodingEnabled
public abstract class CriteriaRepositoryAnnotation extends Annotation {}
