package io.github.stcarolas.enrichedbeans.processor;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.TypeElement;
import com.google.auto.service.AutoService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@AutoService(javax.annotation.processing.Processor.class)
@SupportedAnnotationTypes(
  {
    "io.github.stcarolas.enrichedbeans.annotations.Enrich",
    "io.github.stcarolas.enrichedbeans.annotations.Assisted"
  }
)
public class Processor extends AbstractProcessor {
  private static final Logger log = LogManager.getLogger();

  @Override
  public boolean process(
    java.util.Set<? extends TypeElement> types,
    RoundEnvironment roundEnv
  ) {
    DaggerEnrichProcessorComponent.builder()
      .roundEnv(roundEnv)
      .processingEnv(processingEnv)
      .build()
      .beans()
      .andThen(
        generated -> generated.forEach(
          file -> file.write().onFailure(error -> log.error(error))
        )
      )
      .onFailure(error -> log.error("Cant process: ", error));
    return true;
  }
}
