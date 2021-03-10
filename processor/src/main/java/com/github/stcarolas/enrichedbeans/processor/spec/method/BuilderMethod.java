package  com.github.stcarolas.enrichedbeans.processor.spec.method;

import com.github.stcarolas.enrichedbeans.processor.java.Variable;

import org.immutables.value.Value.Immutable;

import io.vavr.collection.Seq;
import static io.vavr.API.*;

@Immutable public abstract class BuilderMethod
  implements Method {

  abstract String builderClassName();
  abstract String builderMethodName();
  abstract Seq<Variable> objectFields();

}
