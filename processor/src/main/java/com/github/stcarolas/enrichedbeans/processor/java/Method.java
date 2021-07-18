package  com.github.stcarolas.enrichedbeans.processor.java;

import com.squareup.javapoet.TypeName;

import org.immutables.value.Value.Default;
import org.immutables.value.Value.Immutable;

import io.vavr.collection.Seq;
import static io.vavr.API.*;

@Immutable
public interface Method {
  String name();
  Seq<Variable> parameters();
  TypeName returnType();

  @Default default Seq<Annotation> annotations(){
    return Seq();
  }

}
