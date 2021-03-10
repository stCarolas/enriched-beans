package  com.github.stcarolas.enrichedbeans.processor.java;

import org.immutables.value.Value.Immutable;

@Immutable public interface ProvidedField extends Variable {

  default String accessor(){
    return name() + ".get()";
  }

}
