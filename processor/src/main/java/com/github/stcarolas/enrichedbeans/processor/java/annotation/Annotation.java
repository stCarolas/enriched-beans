package com.github.stcarolas.enrichedbeans.processor.java.annotation;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import io.vavr.collection.Map;

public abstract class Annotation {

  abstract public String className();

  abstract public String packageName();

  abstract public Map<String, Object> parameters();

  public AnnotationSpec spec() {
    return parameters()
      .foldLeft(
        AnnotationSpec.builder(ClassName.get(packageName(), className())),
        (builder, parameter) -> builder.addMember(
          parameter._1.substring(0, parameter._1.length() - 2),
          String.format("\"%s\"", parameter._2.toString())
        )
      )
      .build();
  }

}
