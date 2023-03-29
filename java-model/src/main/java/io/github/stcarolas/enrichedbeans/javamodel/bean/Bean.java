package io.github.stcarolas.enrichedbeans.javamodel.bean;

import javax.lang.model.element.ExecutableElement;

import com.squareup.javapoet.TypeName;

import org.immutables.value.Value.Immutable;
import org.immutables.vavr.encodings.VavrEncodingEnabled;

import io.github.stcarolas.enrichedbeans.javamodel.Environment;
import io.github.stcarolas.enrichedbeans.javamodel.annotation.Annotation;
import io.github.stcarolas.enrichedbeans.javamodel.method.Method;
import io.github.stcarolas.enrichedbeans.javamodel.variable.Variable;

import io.vavr.collection.Seq;
import io.vavr.control.Try;

import static io.vavr.API.Try;

@Immutable
@VavrEncodingEnabled
public abstract class Bean {

  abstract public String packageName();

  abstract public String className();

  public Try<TypeName> typeName(){
    return Try(() -> TypeName.get(Class.forName(packageName() + "." + className())));
  }

  abstract public Seq<Annotation> annotations();

  abstract public Seq<Variable> fields();

  abstract public Seq<ExecutableElement> constructors();

  abstract public Seq<Method> methods();

  abstract public Environment env();

  abstract public Boolean isAbstract();

  public boolean missingFieldAnnotatedWith(Class<?> targetAnnotationClass){
    return !hasFieldAnootatedWith(targetAnnotationClass);
  }

  public boolean hasFieldAnootatedWith(Class<?> targetAnnotationClass){
    return fields()
      .filter(field -> field.hasAnnotation(targetAnnotationClass))
      .nonEmpty();
  }

  public boolean missingAnnotation(Class<?> targetAnnotationClass){
    return !hasAnnotation(targetAnnotationClass.getCanonicalName());
  }

  public boolean missingAnnotation(String targetAnnotationClass){
    return !hasAnnotation(targetAnnotationClass);
  }

  public boolean hasAnnotation(String targetAnnotationClass){
    return annotations()
      .find(annotation -> annotation.is(targetAnnotationClass))
      .isDefined();
  }

  @Override
  public String toString() {
    return packageName() + "." + className();
  }

}
