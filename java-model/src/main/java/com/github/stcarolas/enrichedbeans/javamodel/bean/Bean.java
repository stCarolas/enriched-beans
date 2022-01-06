package com.github.stcarolas.enrichedbeans.javamodel.bean;

import static io.vavr.API.Try;

import javax.lang.model.element.ExecutableElement;

import com.github.stcarolas.enrichedbeans.javamodel.annotation.Annotation;
import com.github.stcarolas.enrichedbeans.javamodel.method.Method;
import com.github.stcarolas.enrichedbeans.javamodel.variable.Variable;
import com.squareup.javapoet.TypeName;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.immutables.value.Value.Immutable;
import org.immutables.vavr.encodings.VavrEncodingEnabled;

import io.vavr.collection.Seq;
import io.vavr.control.Try;

@Immutable
public abstract class Bean {
  private static final Logger log = LogManager.getLogger();

  abstract public String packageName();

  abstract public String className();

  abstract public Seq<Annotation> annotations();

  abstract public Seq<Variable> fields();

  abstract public Seq<ExecutableElement> constructors();

  abstract public Seq<Method> methods();

  public Try<TypeName> asType() {
    return Try(
      () -> TypeName.get(
        Class.forName(String.format("%s.%s", packageName(), className()))
      )
    );
  }

  @Override
  public String toString() {
    return packageName() + "." + className();
  }

  @Immutable
  @VavrEncodingEnabled
  public abstract static class BeanImpl extends Bean {}
}
