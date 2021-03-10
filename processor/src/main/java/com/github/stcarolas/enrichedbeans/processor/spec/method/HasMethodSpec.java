package  com.github.stcarolas.enrichedbeans.processor.spec.method;

import com.github.stcarolas.enrichedbeans.processor.java.Annotation;
import com.github.stcarolas.enrichedbeans.processor.java.Variable;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.CodeBlock.Builder;

import org.immutables.value.Value.Immutable;

import io.vavr.collection.Seq;

@Immutable public interface HasMethodSpec {
  MethodSpec spec();
}
