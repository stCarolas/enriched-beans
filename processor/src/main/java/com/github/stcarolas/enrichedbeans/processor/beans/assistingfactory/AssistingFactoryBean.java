package com.github.stcarolas.enrichedbeans.processor.beans.assistingfactory;

import static io.vavr.API.Seq;

import java.util.function.Predicate;

import javax.inject.Inject;
import javax.inject.Named;
import javax.lang.model.element.Modifier;

import com.github.stcarolas.enrichedbeans.annotations.Assisted;
import com.github.stcarolas.enrichedbeans.annotations.Enrich;
import com.github.stcarolas.enrichedbeans.processor.java.Annotation;
import com.github.stcarolas.enrichedbeans.processor.java.Bean;
import com.github.stcarolas.enrichedbeans.processor.java.BeanBuilder;
import com.github.stcarolas.enrichedbeans.processor.java.ImmutableAnnotation;
import com.github.stcarolas.enrichedbeans.processor.java.Variable;
import com.github.stcarolas.enrichedbeans.processor.java.factories.VariableFactory;
import com.github.stcarolas.enrichedbeans.processor.spec.HasVisibility;
import com.github.stcarolas.enrichedbeans.processor.spec.JavaClass;
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
import org.immutables.value.Value.Style;

import io.vavr.collection.Seq;
import io.vavr.control.Option;

@Style(overshadowImplementation=true)
@Immutable public abstract class AssistingFactoryBean 
  implements JavaClass, HasVisibility {

    abstract Bean targetBean();

    @Derived public String packageName(){
      return targetBean().packageName();
    }

    @Derived
    public String name(){
      return targetBean().name() + "Factory";
    }

    @Derived public MethodWithSpec factoryMethod(){
      Boolean detectNamedFields = assistAllInjectedFields();
      return targetBean().builder()
        .filter(builder -> useBuilder())
        .map(rawBuilder -> factoryMethodUsingBuilder(rawBuilder, detectNamedFields))
        .getOrElse(() -> factoryMethodUsingConstructor(detectNamedFields));
    }

    private MethodWithSpec factoryMethodUsingConstructor(boolean detectNamedFields){
      log.debug("Construct factory method using constructor with {} detecting", detectNamedFields);
      return ImmutableFactoryMethod.builder()
        .name("from")
        .returnType(targetBean().type())
        .parameters(notInjectedFields(detectNamedFields))
        .objectFields(injectedFields(detectNamedFields))
        .build();
    }

    private MethodWithSpec factoryMethodUsingBuilder(BeanBuilder beanBuilder,Boolean detectNamedFields){
      log.debug("Construct factory method using builder with %s detecting", detectNamedFields);
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

    @Derived public Constructor constructor(){
      return ImmutableConstructor.builder()
        .classFields(injectedFields(assistAllInjectedFields()))
        .annotations(
          Seq(
            ImmutableAnnotation.builder()
              .className("Inject")
              .packageName("javax.inject")
              .build()
          )
        )
        .build();
    }

    @Default public Seq<HasMethodSpec> methods(){
      return Seq(constructor(), factoryMethod());
    }

    @Derived public TypeSpec spec(){
      Builder spec = TypeSpec.classBuilder(name())
        .addAnnotation(AnnotationSpec.builder(Named.class).build())
        .addFields(
          injectedFields(assistAllInjectedFields())
            .map(Variable::asFieldSpec)
            .toJavaList()
        );
      if (visibility()!= Modifier.DEFAULT){
        spec = spec.addModifiers(visibility());
      }
      spec = methods().foldLeft(
        spec,
        (specBuilder,method) -> specBuilder.addMethod(method.spec())
      );
      return spec.build();
    }

    public AssistingFactoryBean withMethod(Option<HasMethodSpec> method){
      return method
        .map(methods()::append)
        .map(it -> ImmutableAssistingFactoryBean.builder().from(this).methods(it).build())
        .getOrElse(this);
    }

    private Seq<Variable> notInjectedFields(boolean detectNamedFields){
      return targetBean().fields().reject(isAnnotatedBy(annotationOnInjectingFields(detectNamedFields)));
    }

    private Seq<Variable> injectedFields(boolean detectNamedFields){
      Seq<Variable> injectedByMethods = targetBean().methods()
        .filter(method -> method.annotations().exists(anno -> anno.is(Named.class) || anno.is(Inject.class)))
        .map(method -> variableFactory.from(method.name(),method.returnType()));
      return targetBean().fields()
        .filter(isAnnotatedBy(annotationOnInjectingFields(detectNamedFields)))
        .appendAll(injectedByMethods);
    }

    private boolean useBuilder(){
      return targetBean().annotations()
        .filter(anno -> anno.is(Assisted.class))
        .headOption()
        .flatMap(anno -> anno.parameters().get("useBuilder()").map(it -> (Boolean)it))
        .getOrElse(false);
    }

    private boolean assistAllInjectedFields(){
      return targetBean().annotations()
        .filter(anno -> anno.is(Assisted.class))
        .headOption()
        .map(Annotation::parameters)
        .flatMap(params -> params.get("assistAllInjectedFields").map(it -> (Boolean)it))
        .getOrElse(false);
    }

    private Class<?> annotationOnInjectingFields(boolean detectNamedFields){
      return detectNamedFields ? Named.class : Enrich.class;
    }

    private Predicate<Variable> isAnnotatedBy(Class<?> annotationClass){
      return variable -> variable.annotations().exists(annotation -> annotation.is(annotationClass));
    }

    private VariableFactory variableFactory = new VariableFactory();
    static final Logger log = LogManager.getLogger();
}
