package com.github.stcarolas.enrichedbeans.processor.domain.assistingfactory;

import static io.vavr.API.Seq;
import java.util.function.Predicate;
import javax.inject.Inject;
import javax.inject.Named;
import javax.lang.model.element.Modifier;
import com.github.stcarolas.enrichedbeans.annotations.Enrich;
import com.github.stcarolas.enrichedbeans.processor.java.Bean;
import com.github.stcarolas.enrichedbeans.processor.java.BeanBuilder;
import com.github.stcarolas.enrichedbeans.processor.java.Variable;
import com.github.stcarolas.enrichedbeans.processor.java.annotation.ImmutableDefaultAnnotationImpl;
import com.github.stcarolas.enrichedbeans.processor.java.annotation.assisted.AssistedAnnotation;
import com.github.stcarolas.enrichedbeans.processor.java.annotation.enrich.EnrichAnnotation;
import com.github.stcarolas.enrichedbeans.processor.java.annotation.inject.InjectAnnotation;
import com.github.stcarolas.enrichedbeans.processor.java.annotation.named.NamedAnnotation;
import com.github.stcarolas.enrichedbeans.processor.java.factories.VariableFactory;
import com.github.stcarolas.enrichedbeans.processor.domain.SourceFile;
import com.github.stcarolas.enrichedbeans.processor.spec.method.Constructor;
import com.github.stcarolas.enrichedbeans.processor.spec.method.ImmutableConstructor;
import com.github.stcarolas.enrichedbeans.processor.spec.method.ImmutableFactoryMethod;
import com.github.stcarolas.enrichedbeans.processor.spec.method.ImmutableFactoryMethodUsingBuilder;
import com.github.stcarolas.enrichedbeans.processor.spec.method.api.HasMethodSpec;
import com.github.stcarolas.enrichedbeans.processor.spec.method.api.MethodWithSpec;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeSpec.Builder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.immutables.value.Value.Default;
import org.immutables.value.Value.Derived;
import org.immutables.value.Value.Immutable;
import io.vavr.Function2;
import io.vavr.collection.Seq;
import io.vavr.control.Option;
import io.vavr.control.Try;
import static io.vavr.API.*;

@Immutable
public abstract class AssistingFactoryBean implements SourceFile {

  abstract VariableFactory variableFactory();

  abstract Modifier visibility();

  abstract Bean targetBean();

  abstract String factoryMethodName();

  abstract Function2<String, TypeSpec, Try<Void>> writeSourceFileFn();

  abstract String packageName();

  abstract String name();

  @Derived
  public MethodWithSpec factoryMethod() {
    Option<BeanBuilder> builder = useBuilder() ? targetBean().beanBuilder() : None();
    return builder.map(this::factoryMethodUsingBuilder)
      .getOrElse(this::factoryMethodUsingConstructor);
  }

  private MethodWithSpec factoryMethodUsingConstructor() {
    Boolean detectNamedFields = assistAllInjectedFields();
    log.debug(
      "Construct factory method using constructor with {} detecting",
      detectNamedFields
    );
    return ImmutableFactoryMethod.builder()
      .name("from")
      .returnType(targetBean().type())
      .parameters(notInjectedFields(detectNamedFields))
      .objectFields(injectedFields(detectNamedFields))
      .build();
  }

  private MethodWithSpec factoryMethodUsingBuilder(BeanBuilder beanBuilder) {
    Boolean detectNamedFields = assistAllInjectedFields();
    log.debug(
      "Construct factory method using builder with {} detecting",
      detectNamedFields
    );
    return ImmutableFactoryMethodUsingBuilder.builder()
      .beanBuilder(beanBuilder)
      .name("builder")
      .returnType(
        ClassName.get(
          String.format("%s.%s", beanBuilder.packageName(), beanBuilder.className()),
          "Builder"
        )
      )
      .parameters(Seq())
      .objectFields(injectedFields(detectNamedFields))
      .build();
  }

  @Derived
  public Constructor constructor() {
    return ImmutableConstructor.builder()
      .classFields(injectedFields(assistAllInjectedFields()))
      .annotations(
        Seq(
          ImmutableDefaultAnnotationImpl.builder()
            .className("Inject")
            .packageName("javax.inject")
            .build()
        )
      )
      .build();
  }

  @Default
  public Seq<HasMethodSpec> methods() {
    return Seq(constructor(), factoryMethod());
  }

  @Derived
  protected TypeSpec spec() {
    Builder spec = TypeSpec.classBuilder(name())
      .addAnnotation(AnnotationSpec.builder(Named.class).build())
      .addFields(
        injectedFields(assistAllInjectedFields()).map(Variable::asFieldSpec).toJavaList()
      );
    if (visibility() != Modifier.DEFAULT) {
      spec = spec.addModifiers(visibility());
    }
    spec =
      methods()
        .foldLeft(spec, (specBuilder, method) -> specBuilder.addMethod(method.spec()));
    return spec.build();
  }

  public AssistingFactoryBean withMethod(Option<HasMethodSpec> method) {
    return method.map(methods()::append)
      .map(it -> ImmutableAssistingFactoryBean.builder().from(this).methods(it).build())
      .getOrElse(this);
  }

  public Try<Void> write() {
    return writeSourceFileFn().apply(packageName(), spec());
  }

  private Seq<Variable> notInjectedFields(boolean detectNamedFields) {
    return targetBean()
      .fields()
      .reject(shouldBeInjected(detectNamedFields));
  }

  private Seq<Variable> injectedFields(boolean detectNamedFields) {
    Seq<Variable> injectedByMethods = targetBean()
      .methods()
      .filter(
        method -> method.annotations()
          .exists(anno -> anno instanceof NamedAnnotation || anno instanceof InjectAnnotation)
      )
      .map(method -> variableFactory().from(method.name(), method.returnType()));
    return targetBean()
      .fields()
      .filter(shouldBeInjected(detectNamedFields))
      .appendAll(injectedByMethods);
  }

  private boolean useBuilder() {
    return targetBean()
      .annotations()
      .find(anno -> anno instanceof AssistedAnnotation)
      .map(anno -> (AssistedAnnotation) anno)
      .map(AssistedAnnotation::useBuilder)
      .getOrElse(false);
  }

  private boolean assistAllInjectedFields() {
    return targetBean()
      .annotations()
      .find(anno -> anno instanceof AssistedAnnotation)
      .map(anno -> (AssistedAnnotation) anno)
      .map(AssistedAnnotation::assistAllInjectedFields)
      .getOrElse(false);
  }

  private Predicate<Variable> shouldBeInjected(boolean detectNamedFields){
    return variable -> variable.annotations()
      .exists(annotation -> detectNamedFields 
          ? annotation instanceof NamedAnnotation 
          : annotation instanceof EnrichAnnotation
      );
  }

  static final Logger log = LogManager.getLogger();
}
