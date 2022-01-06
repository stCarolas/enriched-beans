package com.github.stcarolas.enrichedbeans.javamodel;

import com.github.stcarolas.enrichedbeans.javamodel.bean.Bean;

import io.vavr.Function1;

public interface CanProcessBeans extends Function1<Bean, SourceFile> {}
