package com.github.stcarolas.enrichedbeans.processor.java;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.immutables.value.Value.Immutable;

@Immutable
public interface BeanBuilder {
  String packageName();
  String className();
  String newBuilderMethod();

  default String build() {
    return String.format("%s.%s.%s()", packageName(), className(), newBuilderMethod());
  }

  static final Logger log = LogManager.getLogger();
}
