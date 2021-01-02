package  com.github.stcarolas.enrichedbeans.processor.spec;

import com.github.stcarolas.enrichedbeans.processor.java.Annotation;

import org.immutables.value.Value.Immutable;

import io.vavr.collection.Seq;

public interface HasAnnotation {
  Seq<Annotation> annotations();
}
