package  com.github.stcarolas.enrichedbeans.processor.java.annotation.named;

import com.github.stcarolas.enrichedbeans.processor.java.annotation.Annotation;

import org.immutables.value.Value.Immutable;
import org.immutables.vavr.encodings.VavrEncodingEnabled;

@Immutable
@VavrEncodingEnabled
public abstract class NamedAnnotation extends Annotation {
}
