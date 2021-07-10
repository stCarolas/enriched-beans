package com.github.stcarolas.enrichedbeans.processor;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import com.github.stcarolas.enrichedbeans.processor.functions.FindAndEnrichBeans;
import com.github.stcarolas.enrichedbeans.processor.spec.SpecModule;
import dagger.BindsInstance;
import dagger.Component;

@Component(modules = SpecModule.class)
public interface EnrichProcessorComponent {
  FindAndEnrichBeans findAndEnrichBeans();

  @Component.Builder
  interface Builder {
    @BindsInstance
    Builder roundEnv(RoundEnvironment roundEnv);

    @BindsInstance
    Builder processingEnv(ProcessingEnvironment processingEnv);

    EnrichProcessorComponent build();
  }
}
