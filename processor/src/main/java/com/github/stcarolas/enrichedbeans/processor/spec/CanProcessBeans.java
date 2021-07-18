package com.github.stcarolas.enrichedbeans.processor.spec;

import com.github.stcarolas.enrichedbeans.processor.java.Bean;
import com.github.stcarolas.enrichedbeans.processor.domain.SourceFile;
import io.vavr.Function1;

public interface CanProcessBeans extends Function1<Bean, SourceFile> {}
