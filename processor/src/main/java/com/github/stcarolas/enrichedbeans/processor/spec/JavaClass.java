package com.github.stcarolas.enrichedbeans.processor.spec;

import javax.annotation.processing.ProcessingEnvironment;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.vavr.control.Try;

public interface JavaClass {

    String packageName();
    TypeSpec spec();
    
    default Try<Void> writeTo(ProcessingEnvironment env){
      return Try.run(
        () -> JavaFile.builder(packageName(), spec())
          .build()
          .writeTo(env.getFiler())
      ).onFailure(error -> log.error(error));
    }

    static final Logger log = LogManager.getLogger();
}
