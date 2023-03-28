package io.github.stcarolas.enrichedbeans.javamodel.variable;

import org.junit.jupiter.api.Test;
import io.vavr.collection.Map;
import io.github.stcarolas.enrichedbeans.javamodel.annotation.Annotation;
import io.github.stcarolas.enrichedbeans.javamodel.annotation.SecondTestAnnotation;
import io.github.stcarolas.enrichedbeans.javamodel.annotation.TestAnnotation;
import io.github.stcarolas.enrichedbeans.javamodel.variable.ImmutableVariableImpl.Builder;

import com.squareup.javapoet.TypeName;

import static org.junit.jupiter.api.Assertions.*;
import static io.vavr.API.*;

public class VariableTest {

  @Test
  public void hasAnnotationMethodWorksCorrectly() {
    Builder baseVariable = ImmutableVariableImpl.builder()
      .name("testVariable")
      .type(TypeName.BOOLEAN);

    Variable variableWithTestAnnotation = baseVariable
      .addAnnotations(new TestAnnotation())
      .build();
    assertTrue(variableWithTestAnnotation.hasAnnotation(TestAnnotation.class));
    assertFalse(variableWithTestAnnotation.hasAnnotation(SecondTestAnnotation.class));

    Variable variableWithBothAnnotations = baseVariable
      .addAnnotations(new TestAnnotation())
      .build();
    assertTrue(variableWithBothAnnotations.hasAnnotation(TestAnnotation.class));
    assertTrue(variableWithBothAnnotations.hasAnnotation(SecondTestAnnotation.class));
  }

  @Test
  public void testRemovingAnnotation() {
    Variable variable = ImmutableVariableImpl.builder()
      .name("testVariable")
      .type(TypeName.BOOLEAN)
      .addAnnotations(new TestAnnotation())
      .build();
    Variable updatedVariable = variable.removeAnnotation(
      TestAnnotation.class.getPackageName(),
      TestAnnotation.class.getSimpleName()
    );
    assertFalse(variable.annotations().isEmpty());
    assertTrue(updatedVariable.annotations().isEmpty());
  }

  @Test
  public void testCheckingIfVariableHasAnnotationsAfterRemoving() {
    Variable variable = ImmutableVariableImpl.builder()
      .name("testVariable")
      .type(TypeName.BOOLEAN)
      .addAnnotations(new TestAnnotation())
      .build();
    Variable updatedVariable = variable.removeAnnotation(
      TestAnnotation.class.getPackageName(),
      TestAnnotation.class.getSimpleName()
    );
    assertTrue(variable.hasAnnotation(TestAnnotation.class));
    assertFalse(updatedVariable.hasAnnotation(TestAnnotation.class));
  }

}
