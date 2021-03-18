package com.github.stcarolas.enrichedbeans.micronaut;

import java.util.function.Function;

import javax.inject.Named;

import com.github.stcarolas.enrichedbeans.annotations.Assisted;

import org.immutables.value.Value.Immutable;

@Assisted(useBuilder = true)
@Immutable public abstract class AssistedObject {

  abstract public String name();

  @Named("ProcessName")
  abstract public Function<String, String> processNameFn();

  public String processName(){
    return processNameFn().apply(name());
  }

}
