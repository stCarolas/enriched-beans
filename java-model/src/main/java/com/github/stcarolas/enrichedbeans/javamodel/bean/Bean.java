package com.github.stcarolas.enrichedbeans.javamodel.bean;

import javax.lang.model.element.ExecutableElement;

import com.github.stcarolas.enrichedbeans.javamodel.Environment;
import com.github.stcarolas.enrichedbeans.javamodel.annotation.Annotation;
import com.github.stcarolas.enrichedbeans.javamodel.method.Method;
import com.github.stcarolas.enrichedbeans.javamodel.variable.Variable;
import com.squareup.javapoet.TypeName;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.immutables.value.Value.Immutable;
import org.immutables.vavr.encodings.VavrEncodingEnabled;

import io.vavr.collection.Seq;

@Immutable
public abstract class Bean {
  private static final Logger log = LogManager.getLogger();

  abstract public String packageName();

  abstract public String className();

  abstract public Seq<Annotation> annotations();

  abstract public Seq<Variable> fields();

  abstract public Seq<ExecutableElement> constructors();

  abstract public Seq<Method> methods();

  abstract public Environment env();

  @Override
  public String toString() {
    return packageName() + "." + className();
  }

  @Immutable
  @VavrEncodingEnabled
  public abstract static class BeanImpl extends Bean {}
}
