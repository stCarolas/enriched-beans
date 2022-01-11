package io.github.stcarolas.enrichedbeans.processor;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;

import io.github.stcarolas.enrichedbeans.javamodel.bean.Bean;
import io.github.stcarolas.enrichedbeans.javamodel.bean.GeneratedBean;
import io.github.stcarolas.enrichedbeans.processor.modules.AnnotationFactoriesModule;
import io.github.stcarolas.enrichedbeans.processor.modules.BeanFactoriesModule;
import io.github.stcarolas.enrichedbeans.processor.modules.BeansModule;
import io.github.stcarolas.enrichedbeans.processor.modules.MethodFactoriesModule;
import io.github.stcarolas.enrichedbeans.processor.modules.SourceFilesModule;
import io.github.stcarolas.enrichedbeans.processor.modules.VariableFactoriesModule;
import dagger.BindsInstance;
import dagger.Component;
import io.vavr.collection.Seq;
import io.vavr.control.Try;

@Component(
  modules = {
    VariableFactoriesModule.class,
    AnnotationFactoriesModule.class,
    MethodFactoriesModule.class,
    BeanFactoriesModule.class,
    BeansModule.class,
    SourceFilesModule.class
  }
)
public interface EnrichProcessorComponent {
  Try<Seq<GeneratedBean>> beans();

  @Component.Builder
  interface Builder {
    @BindsInstance
    Builder roundEnv(RoundEnvironment roundEnv);

    @BindsInstance
    Builder processingEnv(ProcessingEnvironment processingEnv);

    EnrichProcessorComponent build();
  }
}
