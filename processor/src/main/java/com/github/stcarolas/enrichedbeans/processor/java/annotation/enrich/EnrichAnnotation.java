package  com.github.stcarolas.enrichedbeans.processor.java.annotation.enrich;

import com.github.stcarolas.enrichedbeans.processor.java.annotation.Annotation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.immutables.value.Value.Immutable;
import org.immutables.vavr.encodings.VavrEncodingEnabled;

@Immutable
@VavrEncodingEnabled
public abstract class EnrichAnnotation extends Annotation {

  private static final Logger log = LogManager.getLogger();
}
