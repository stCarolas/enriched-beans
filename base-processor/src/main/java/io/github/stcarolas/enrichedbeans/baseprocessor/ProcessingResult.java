package  io.github.stcarolas.enrichedbeans.baseprocessor;

import org.immutables.value.Value.Immutable;
import org.immutables.vavr.encodings.VavrEncodingEnabled;

import io.github.stcarolas.enrichedbeans.javamodel.bean.GeneratedBean;
import io.vavr.collection.Seq;
import io.vavr.control.Try;

public abstract class ProcessingResult {

  abstract public Seq<Try<Seq<GeneratedBean>>> beans();

  abstract public Boolean isFinished();

  @Immutable
  @VavrEncodingEnabled
  public static abstract class ProcessingResultImpl extends ProcessingResult {
  }
}
