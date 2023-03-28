package io.github.stcarolas.enrichedbeans.javamodel.annotation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.squareup.javapoet.AnnotationSpec;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static io.vavr.API.*;

public class AnnotationTest {

  @Test
  public void testIsMethodWorksCorrectly() {
    Annotation annotation = ImmutableAnnotationImpl.builder()
      .packageName("io.github.stcarolas.enrichedbeans.javamodel.annotation")
      .className("TestAnnotation")
      .parameters(Map("value", "test"))
      .build();
    assertTrue(annotation.is(TestAnnotation.class));

    ImmutableAnnotationImpl annotationWithWrongClassName = ImmutableAnnotationImpl.builder().from(annotation)
      .className("WrongClassName")
      .build();
    assertFalse(annotationWithWrongClassName.is(TestAnnotation.class));

    ImmutableAnnotationImpl annotationWithWrongPackageName = ImmutableAnnotationImpl.builder().from(annotation)
      .packageName("WrongPackageName")
      .build();
    assertFalse(annotationWithWrongPackageName.is(TestAnnotation.class));
  }

  @Test
  @Disabled
  public void testCreateCorrectSpec() {
    Annotation annotation = ImmutableAnnotationImpl.builder()
      .packageName("io.github.stcarolas.enrichedbeans.javamodel.annotation")
      .className("TestAnnotation")
      .parameters(Map("value", "test"))
      .build();
    AnnotationSpec expected = AnnotationSpec.builder(TestAnnotation.class)
      .addMember("value", "$S", "test")
      .build();
    AnnotationSpec actual = annotation.spec();

    // assertEquals(expected, actual);
  }

}
