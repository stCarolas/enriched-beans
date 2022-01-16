package io.github.stcarolas.enrichedbeans.micronaut.immutables;

import java.util.function.Function;
import javax.inject.Inject;
import javax.inject.Named;

import org.immutables.value.Value.Immutable;

import io.github.stcarolas.enrichedbeans.annotations.Assisted;

@Immutable
@Assisted
public abstract class ImmutableRichObject {

  @Inject
  abstract Function<String, String> processName();

  @Inject
  @Named("RichObjectName")
  abstract String name();

  abstract String value();

}
