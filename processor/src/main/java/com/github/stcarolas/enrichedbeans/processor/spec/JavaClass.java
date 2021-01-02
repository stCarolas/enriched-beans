package com.github.stcarolas.enrichedbeans.processor.spec;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Modifier;

import com.github.stcarolas.enrichedbeans.processor.java.Annotation;
import com.github.stcarolas.enrichedbeans.processor.spec.method.ImmutableConstructor;
import com.github.stcarolas.enrichedbeans.processor.spec.method.Method;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.vavr.collection.Seq;
import io.vavr.control.Try;
import static io.vavr.API.*;

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
