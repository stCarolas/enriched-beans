package  com.github.stcarolas.enrichedbeans.processor.domain;

import io.vavr.control.Try;

public interface SourceFile {
    Try<Void> write();
}
