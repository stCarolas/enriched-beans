package io.github.stcarolas.enrichedbeans.javamodel.bean;

import javax.lang.model.element.ExecutableElement;

import org.immutables.value.Value.Immutable;
import org.immutables.vavr.encodings.VavrEncodingEnabled;

import io.github.stcarolas.enrichedbeans.javamodel.Environment;
import io.github.stcarolas.enrichedbeans.javamodel.annotation.Annotation;
import io.github.stcarolas.enrichedbeans.javamodel.method.Method;
import io.github.stcarolas.enrichedbeans.javamodel.variable.Variable;

import io.vavr.collection.Seq;

@Immutable
public abstract class Bean {

  abstract public String packageName();

  abstract public String className();

  abstract public Seq<Annotation> annotations();

  abstract public Seq<Variable> fields();

  abstract public Seq<ExecutableElement> constructors();

  abstract public Seq<Method> methods();

  abstract public Environment env();

  abstract public Boolean isAbstract();

  public boolean missingAnnotation(Class<?> targetAnnotationClass){
    return !hasAnnotation(targetAnnotationClass);
  }

  public boolean hasAnnotation(Class<?> targetAnnotationClass){
    return annotations()
      .find(annotation -> annotation.is(targetAnnotationClass))
      .isEmpty();
  }

  @Override
  public String toString() {
    return packageName() + "." + className();
  }

  @Immutable
  @VavrEncodingEnabled
  public abstract static class BeanImpl extends Bean {}
}
