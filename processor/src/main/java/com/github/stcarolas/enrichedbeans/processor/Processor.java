package com.github.stcarolas.enrichedbeans.processor;

import java.util.function.Function;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.TypeElement;

import com.github.stcarolas.enrichedbeans.processor.functions.FindBeans;
import com.github.stcarolas.enrichedbeans.processor.java.Bean;
import com.github.stcarolas.enrichedbeans.processor.spec.JavaClass;
import com.google.auto.service.AutoService;

import io.vavr.Function1;
import io.vavr.Function2;
import io.vavr.collection.Seq;
import io.vavr.control.Try;
import static io.vavr.API.*;

@AutoService(javax.annotation.processing.Processor.class)
@SupportedAnnotationTypes("com.github.stcarolas.enrichedbeans.annotations.Enrich")
public class Processor extends AbstractProcessor {

  @Override
  public boolean process(
    java.util.Set<? extends TypeElement> annotations,
    RoundEnvironment roundEnv
  ) {
    return DaggerEnrichProcessorComponent.builder().build()
      .enrichBeansFunction().apply(roundEnv, processingEnv);
  }

}
