package io.github.stcarolas.enrichedbeans.javamodel.annotation;

import io.vavr.collection.Map;
import static io.vavr.API.Map;

public class TestAnnotation extends Annotation {

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
