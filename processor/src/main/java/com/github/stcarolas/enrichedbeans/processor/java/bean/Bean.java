package com.github.stcarolas.enrichedbeans.processor.java.bean;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

import com.github.stcarolas.enrichedbeans.processor.java.BeanBuilder;
import com.github.stcarolas.enrichedbeans.processor.java.Method;
import com.github.stcarolas.enrichedbeans.processor.java.Variable;
import com.github.stcarolas.enrichedbeans.processor.java.annotation.Annotation;
import com.github.stcarolas.enrichedbeans.processor.java.annotation.assisted.AssistedAnnotation;
import com.github.stcarolas.enrichedbeans.processor.java.annotation.enrich.EnrichAnnotation;
import com.github.stcarolas.enrichedbeans.processor.java.annotation.inject.InjectAnnotation;
import com.github.stcarolas.enrichedbeans.processor.java.annotation.named.NamedAnnotation;
import com.github.stcarolas.enrichedbeans.processor.java.factories.VariableFactory;
import com.github.stcarolas.enrichedbeans.processor.spec.method.ImmutableFactoryMethod;
import com.github.stcarolas.enrichedbeans.processor.spec.method.ImmutableFactoryMethodUsingBuilder;
import com.github.stcarolas.enrichedbeans.processor.spec.method.api.MethodWithSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.immutables.value.Value.Immutable;
import io.vavr.collection.Seq;
import io.vavr.control.Option;
import static io.vavr.API.*;

import java.util.function.Predicate;

@Immutable
public abstract class Bean {
  private static final Logger log = LogManager.getLogger();
  private static final String  DEFAULT_FACTORY_METHOD_NAME = "from";
  private static final String  DEFAULT_BUILDER_METHOD_NAME = "builder";

  abstract public String name();

  abstract public String packageName();

  abstract public Option<String> factoryMethodName();

  abstract public TypeName type();

  abstract public Seq<TypeElement> subtypes();

  abstract public Seq<Variable> fields();

  abstract public Seq<ExecutableElement> constructors();

  abstract public Seq<Annotation> annotations();

  abstract public Seq<Method> methods();

  abstract public Option<BeanBuilder> beanBuilder();

  abstract public VariableFactory variableFactory();

  public String toString() {
    return packageName() + "." + name();
  }

  public boolean assistAllInjectedFields() {
    return annotations()
      .find(anno -> anno instanceof AssistedAnnotation)
      .map(anno -> (AssistedAnnotation) anno)
      .map(AssistedAnnotation::assistAllInjectedFields)
      .getOrElse(false);
  }

  public MethodWithSpec createFactoryMethod() {
    Option<BeanBuilder> builder = useBuilder() ? beanBuilder() : None();
    return builder.map(this::factoryMethodUsingBuilder)
      .getOrElse(this::factoryMethodUsingConstructor);
  }

  private boolean useBuilder() {
    return annotations()
      .find(anno -> anno instanceof AssistedAnnotation)
      .map(anno -> (AssistedAnnotation) anno)
      .map(AssistedAnnotation::useBuilder)
      .getOrElse(false);
  }

  private MethodWithSpec factoryMethodUsingConstructor() {
    log.debug("Construct factory method using constructor with");
    return ImmutableFactoryMethod.builder()
      .name(factoryMethodName().getOrElse(DEFAULT_FACTORY_METHOD_NAME))
      .returnType(type())
      .parameters(notInjectedFields())
      .objectFields(injectedFields())
      .build();
  }

  private MethodWithSpec factoryMethodUsingBuilder(BeanBuilder beanBuilder) {
    log.debug("Construct factory method using builder");
    return ImmutableFactoryMethodUsingBuilder.builder()
      .beanBuilder(beanBuilder)
      .name(DEFAULT_BUILDER_METHOD_NAME)
      .returnType(
        ClassName.get(
          String.format("%s.%s", beanBuilder.packageName(), beanBuilder.className()),
          "Builder"
        )
      )
      .parameters(Seq())
      .objectFields(injectedFields())
      .build();
  }

  public Seq<Variable> notInjectedFields() {
    return fields().reject(shouldBeInjected(assistAllInjectedFields()));
  }

  public Seq<Variable> injectedFields() {
    Seq<Variable> injectedByMethods = methods()
      .filter(
        method -> method.annotations()
          .exists(anno -> anno instanceof NamedAnnotation || anno instanceof InjectAnnotation)
      )
      .map(method -> variableFactory().from(method.name(), method.returnType()));
    return fields()
      .filter(shouldBeInjected(assistAllInjectedFields()))
      .appendAll(injectedByMethods);
  }

  private Predicate<Variable> shouldBeInjected(boolean detectNamedFields){
    return variable -> variable.annotations()
      .exists(annotation -> detectNamedFields 
          ? annotation instanceof NamedAnnotation 
          : annotation instanceof EnrichAnnotation
      );
  }

}
