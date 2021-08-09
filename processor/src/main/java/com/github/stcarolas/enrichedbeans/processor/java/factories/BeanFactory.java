package com.github.stcarolas.enrichedbeans.processor.java.factories;

import javax.inject.Inject;
import javax.inject.Named;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import com.github.stcarolas.enrichedbeans.processor.java.bean.Bean;
import com.github.stcarolas.enrichedbeans.processor.java.bean.ImmutableBean;
import com.github.stcarolas.enrichedbeans.processor.java.BeanBuilder;
import com.github.stcarolas.enrichedbeans.processor.java.ImmutableBeanBuilder;
import com.github.stcarolas.enrichedbeans.processor.java.Method;
import com.github.stcarolas.enrichedbeans.processor.java.Variable;
import com.github.stcarolas.enrichedbeans.processor.java.annotation.Annotation;
import com.github.stcarolas.enrichedbeans.processor.java.annotation.AnnotationFactory;
import com.squareup.javapoet.TypeName;
import io.vavr.collection.List;
import io.vavr.collection.Seq;
import io.vavr.control.Option;
import static io.vavr.API.*;

@Named("BeanFactory")
public class BeanFactory {
  private VariableFactory variableFactory;
  private AnnotationFactory annotationFactory;
  private MethodFactory methodFactory;

  @Inject
  public BeanFactory(
    VariableFactory fieldFactory,
    AnnotationFactory annotationFactory,
    MethodFactory methodFactory
  ) {
    this.variableFactory = fieldFactory;
    this.annotationFactory = annotationFactory;
    this.methodFactory = methodFactory;
  }

  public Bean from(Element origin) {
    //String superclass = ((DeclaredType) ((TypeElement) origin).getSuperclass())
      //.toString();
    //System.out.println(String.format("superclass: %s", superclass));
    return ImmutableBean.builder()
      .variableFactory(variableFactory)
      .name(origin.getSimpleName().toString())
      .packageName(packageName(origin))
      .type(TypeName.get(origin.asType()))
      .subtypes(subtypes(origin))
      .fields(fields(origin))
      .constructors(constructors(origin))
      .annotations(annotations(origin))
      .methods(methods(origin))
      .beanBuilder(beanBuilder(origin))
      .build();
  }

  private String name(Element origin) {
    return origin.getSimpleName().toString();
  }

  private String packageName(Element original) {
    String fullName = ((TypeElement) original).getQualifiedName().toString();
    String packageName = "";
    int lastDot = fullName.lastIndexOf('.');
    if (lastDot > 0) {
      packageName = fullName.substring(0, lastDot);
    }
    return packageName;
  }

  private Seq<TypeElement> subtypes(Element origin) {
    return List.ofAll(origin.getEnclosedElements())
      .filter(element -> element.getKind().isInterface())
      .map(element -> (TypeElement) element);
  }

  private Seq<Variable> fields(Element origin) {
    return List.ofAll(origin.getEnclosedElements())
      .filter(element -> element.getKind().isField())
      .map(variableFactory::from);
  }

  private Seq<ExecutableElement> constructors(Element origin) {
    return List.ofAll(origin.getEnclosedElements())
      .filter(element -> element.getKind().equals(ElementKind.CONSTRUCTOR))
      .map($ -> (ExecutableElement) $);
  }

  private Seq<Annotation> annotations(Element original) {
    return List.ofAll(original.getAnnotationMirrors()).map(annotationFactory::from);
  }

  private Seq<Method> methods(Element original) {
    return List.ofAll(original.getEnclosedElements())
      .filter(element -> element.getKind().equals(ElementKind.METHOD))
      .map(methodFactory::from);
  }

  private Option<BeanBuilder> beanBuilder(Element origin) {
    return Some(
      ImmutableBeanBuilder.builder()
        .className("Immutable" + name(origin))
        .packageName(packageName(origin))
        .newBuilderMethod("builder")
        .build()
    );
  }
}
