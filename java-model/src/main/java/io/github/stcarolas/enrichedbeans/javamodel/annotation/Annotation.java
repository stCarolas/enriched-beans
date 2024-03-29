package io.github.stcarolas.enrichedbeans.javamodel.annotation;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.immutables.value.Value.Immutable;
import org.immutables.vavr.encodings.VavrEncodingEnabled;

import io.vavr.collection.Map;

public abstract class Annotation {

  private Logger log =  LogManager.getLogger();

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

  public boolean is(Class<?> annotationClass){
    return is(annotationClass.getCanonicalName());
  }

  public boolean is(String annotationClass){
    return (packageName() + "." + className()).equals(annotationClass);
  }


  @Immutable
  @VavrEncodingEnabled
  public abstract static class AnnotationImpl extends Annotation {}
}
