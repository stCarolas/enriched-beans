package  com.github.stcarolas.enrichedbeans.processor.spec;

import com.github.stcarolas.enrichedbeans.processor.spec.method.Method;

import io.vavr.collection.Seq;

public interface HasMethods {
  Seq<Method> methods();
}
