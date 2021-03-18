package  com.github.stcarolas.enrichedbeans.processor.java;

import com.squareup.javapoet.TypeName;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.immutables.value.Value.Default;
import org.immutables.value.Value.Immutable;

import io.vavr.collection.Seq;
import static io.vavr.API.*;

@Immutable public interface Method {
  String name();
  Seq<Variable> parameters();
  TypeName returnType();

  @Default default Seq<Annotation> annotations(){
    return Seq();
  }

  static final Logger log = LogManager.getLogger();

}
