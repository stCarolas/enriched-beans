package io.github.stcarolas.enrichedbeans.javamodel.variable;

import org.junit.jupiter.api.Test;
import io.vavr.collection.Map;
import io.github.stcarolas.enrichedbeans.javamodel.annotation.Annotation;
import io.github.stcarolas.enrichedbeans.javamodel.variable.ImmutableVariableImpl.Builder;
import com.squareup.javapoet.TypeName;
import static org.junit.jupiter.api.Assertions.*;
import static io.vavr.API.*;

public class VariableTest {

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
    assertTrue(updatedVariable.annotations().isEmpty());
  }

  public static class TestAnnotation extends Annotation {

    @Override
    public String className() {
      return this.getClass().getSimpleName();
    }

    @Override
    public String packageName() {
      return this.getClass().getPackageName();
    }

    @Override
    public Map<String, Object> parameters() {
      return Map();
    }
  }
}
