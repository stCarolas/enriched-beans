package com.github.stcarolas.enrichedbeans.javamodel;

import io.vavr.control.Try;

public interface SourceFile {
  Try<SourceFile> write();
}
