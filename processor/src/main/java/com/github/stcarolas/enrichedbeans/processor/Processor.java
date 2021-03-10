package com.github.stcarolas.enrichedbeans.processor;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.TypeElement;

import com.google.auto.service.AutoService;


@AutoService(javax.annotation.processing.Processor.class)
@SupportedAnnotationTypes("com.github.stcarolas.enrichedbeans.annotations.Enrich")
public class Processor extends AbstractProcessor {

  @Override
  public boolean process(
    java.util.Set<? extends TypeElement> annotations,
    RoundEnvironment roundEnv
  ) {
    return DaggerEnrichProcessorComponent.builder().build()
      .findAndEnrichBeans().apply(roundEnv, processingEnv);
  }

}
