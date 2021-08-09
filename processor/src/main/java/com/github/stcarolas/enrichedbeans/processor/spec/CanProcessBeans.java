package com.github.stcarolas.enrichedbeans.processor.spec;

import com.github.stcarolas.enrichedbeans.processor.domain.SourceFile;
import com.github.stcarolas.enrichedbeans.processor.java.bean.Bean;

import io.vavr.Function1;

public interface CanProcessBeans extends Function1<Bean, SourceFile> {}
