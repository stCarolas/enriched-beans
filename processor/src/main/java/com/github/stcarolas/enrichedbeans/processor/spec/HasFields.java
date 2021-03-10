package  com.github.stcarolas.enrichedbeans.processor.spec;

import com.github.stcarolas.enrichedbeans.processor.java.Variable;
import io.vavr.collection.Seq;

public interface HasFields {
  Seq<Variable> fields();
}
