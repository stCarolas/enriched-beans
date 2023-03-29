package io.github.stcarolas.enrichedbeans.immutablescriteriaspring;

import static io.vavr.API.*;

import com.google.auto.service.AutoService;
import io.github.stcarolas.enrichedbeans.baseprocessor.BaseEnrichedBeansProcessor;
import io.github.stcarolas.enrichedbeans.baseprocessor.BeanProcessorComponent;
import io.github.stcarolas.enrichedbeans.immutablescriteriaspring.dagger.DaggerImmutablesCriteriaSpringComponent;
import io.vavr.collection.Seq;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;

@AutoService(javax.annotation.processing.Processor.class)
@SupportedAnnotationTypes({ "org.immutables.criteria.Criteria.Repository","org.immutables.criteria.Criteria" })
public class ImmutablesCriteriaSpringProcessor
  extends BaseEnrichedBeansProcessor {

  @Override
  public Seq<BeanProcessorComponent> components(RoundEnvironment roundEnv) {
    return Seq(
      DaggerImmutablesCriteriaSpringComponent
        .builder()
        .roundEnv(roundEnv)
        .processingEnv(processingEnv)
        .build()
    );
  }
}
