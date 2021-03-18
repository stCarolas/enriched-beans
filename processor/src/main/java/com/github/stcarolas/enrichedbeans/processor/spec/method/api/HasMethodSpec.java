package  com.github.stcarolas.enrichedbeans.processor.spec.method.api;

import com.squareup.javapoet.MethodSpec;
import org.immutables.value.Value.Immutable;

@Immutable public interface HasMethodSpec {
  MethodSpec spec();
}
