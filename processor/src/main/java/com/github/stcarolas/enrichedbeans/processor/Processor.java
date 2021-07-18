package com.github.stcarolas.enrichedbeans.processor;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.TypeElement;
import com.github.stcarolas.enrichedbeans.processor.domain.Environment;
import com.github.stcarolas.enrichedbeans.processor.domain.SourceFile;
import com.google.auto.service.AutoService;

@AutoService(javax.annotation.processing.Processor.class)
@SupportedAnnotationTypes(
  {
    "com.github.stcarolas.enrichedbeans.annotations.Enrich",
    "com.github.stcarolas.enrichedbeans.annotations.Assisted"
  }
)
public class Processor extends AbstractProcessor {

  @Override
  public boolean process(
    java.util.Set<? extends TypeElement> types,
    RoundEnvironment roundEnv
  ) {
    DaggerEnrichProcessorComponent.builder()
      .environment(new Environment(roundEnv, processingEnv))
      .build()
      .generated()
      .map(SourceFile::write);
    return true;
  }
}
