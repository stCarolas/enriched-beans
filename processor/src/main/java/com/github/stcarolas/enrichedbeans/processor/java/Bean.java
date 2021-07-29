package com.github.stcarolas.enrichedbeans.processor.java;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

import com.github.stcarolas.enrichedbeans.processor.java.annotation.Annotation;
import com.squareup.javapoet.TypeName;
import org.immutables.value.Value.Immutable;
import io.vavr.collection.Seq;
import io.vavr.control.Option;

@Immutable
public abstract class Bean {

  abstract public String name();

  abstract public String packageName();

  abstract public TypeName type();

  abstract public Seq<TypeElement> subtypes();

  abstract public Seq<Variable> fields();

  abstract public Seq<ExecutableElement> constructors();

  abstract public Seq<Annotation> annotations();

  abstract public Seq<Method> methods();

  abstract public Option<BeanBuilder> beanBuilder();

  public String toString() {
    return packageName() + "." + name();
  }
}
