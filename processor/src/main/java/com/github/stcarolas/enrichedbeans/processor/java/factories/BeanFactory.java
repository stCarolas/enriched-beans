package com.github.stcarolas.enrichedbeans.processor.java.factories;

import javax.inject.Inject;
import javax.inject.Named;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;

import com.github.stcarolas.enrichedbeans.processor.java.Bean;
import com.github.stcarolas.enrichedbeans.processor.java.ImmutableBean;

@Named("BeanFactory")
public class BeanFactory {
  private VariableFactory fieldFactory;
  private AnnotationFactory annotationFactory;
  private MethodFactory methodFactory;

  @Inject
  public BeanFactory(
    VariableFactory fieldFactory,
    AnnotationFactory annotationFactory,
    MethodFactory methodFactory
  ) {
    this.fieldFactory = fieldFactory;
    this.annotationFactory = annotationFactory;
    this.methodFactory = methodFactory;
  }

  public Bean from(Element original) {
    String superclass = ((DeclaredType)((TypeElement) original).getSuperclass()).toString();
    System.out.println(String.format("superclass: %s", superclass));
    return ImmutableBean.builder()
      .annotationFactory(annotationFactory)
      .fieldFactory(fieldFactory)
      .methodFactory(methodFactory)
      .original(original)
      .build();
  }
}
