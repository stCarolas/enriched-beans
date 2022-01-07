package com.github.stcarolas.enrichedbeans.micronaut.simple;

import java.util.function.Function;

import javax.inject.Named;

import com.github.stcarolas.enrichedbeans.annotations.Enrich;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RichObject {
  private final String name;

  @Enrich
  private final Function<String, String> processName;

  @Enrich
  @Named("ProcessName")
  private final Function<String, String> processName2;

  public String processName() {
    return processName.apply(name);
  }
}
