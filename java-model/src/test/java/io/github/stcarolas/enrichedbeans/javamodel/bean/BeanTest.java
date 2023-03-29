package io.github.stcarolas.enrichedbeans.javamodel.bean;

import javax.lang.model.element.ExecutableElement;

import org.junit.jupiter.api.Test;

import io.github.stcarolas.enrichedbeans.javamodel.Environment;
import io.github.stcarolas.enrichedbeans.javamodel.annotation.Annotation;
import io.github.stcarolas.enrichedbeans.javamodel.annotation.ImmutableAnnotationImpl;
import io.github.stcarolas.enrichedbeans.javamodel.method.Method;
import io.github.stcarolas.enrichedbeans.javamodel.variable.Variable;
import io.vavr.collection.Seq;

import static io.vavr.API.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

public class BeanTest {
  Environment env = mock(Environment.class);
  String annotationClassName = "className";
  String annotationPackageName = "packageName";

  @Test
  public void testHasAnnotationByCanonicalName() {
    Bean bean = new Bean() {
      @Override
      public String packageName() {
        return "io.github.stcarolas.enrichedbeans.javamodel.bean";
      }

      @Override
      public String className() {
        return "BeanTest";
      }

      @Override
      public Seq<Annotation> annotations() {
        return Seq(ImmutableAnnotationImpl.builder()
          .className(annotationClassName)
          .packageName(annotationPackageName)
          .build());
      }

      @Override
      public Seq<Variable> fields() {
        return Seq();
      }

      @Override
      public Seq<ExecutableElement> constructors() {
        return Seq();
      }

      @Override
      public Seq<Method> methods() {
        return Seq();
      }

      @Override
      public Environment env() {
        return env;
      }

      @Override
      public Boolean isAbstract() {
        return false;
      }
    };
    assertTrue(bean.hasAnnotation(annotationPackageName + "." + annotationClassName));
  }
}
