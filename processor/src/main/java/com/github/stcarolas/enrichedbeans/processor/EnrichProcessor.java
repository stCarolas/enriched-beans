package com.github.stcarolas.enrichedbeans.processor;

import java.util.function.Function;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.TypeElement;
import com.github.stcarolas.enrichedbeans.processor.functions.CreateSpec;
import com.github.stcarolas.enrichedbeans.processor.functions.FindBeans;
import com.github.stcarolas.enrichedbeans.processor.functions.WriteSpec;
import com.google.auto.service.AutoService;
import io.vavr.Function2;
import io.vavr.collection.Seq;
import io.vavr.control.Try;

@AutoService(Processor.class)
@SupportedAnnotationTypes("com.github.stcarolas.enrichedbeans.annotations.Enrich")
public class EnrichProcessor extends AbstractProcessor {

  @Override
  public boolean process(
      java.util.Set<? extends TypeElement> annotations,
      RoundEnvironment roundEnv
  ) {
      return findBeans.apply(roundEnv)
          .map(bean -> createSpec.apply(bean, processingEnv))
          .map(spec -> writeSpec.apply(spec, processingEnv))
          .filter(Try::isFailure)
          .isEmpty();
  }

  private Function<RoundEnvironment, Seq<TargetBean>> findBeans = 
    new FindBeans();
    
  private Function2<TargetBean, ProcessingEnvironment, AssistingFactorySpec> createSpec = 
    new CreateSpec();

  private Function2<AssistingFactorySpec, ProcessingEnvironment, Try<Void>> writeSpec = 
    new WriteSpec();
}
