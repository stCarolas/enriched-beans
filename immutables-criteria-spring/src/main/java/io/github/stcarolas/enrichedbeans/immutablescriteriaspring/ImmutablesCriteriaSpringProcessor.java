package io.github.stcarolas.enrichedbeans.immutablescriteriaspring;

import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;

import com.google.auto.service.AutoService;

import io.github.stcarolas.enrichedbeans.baseprocessor.BaseEnrichedBeansProcessor;
import io.github.stcarolas.enrichedbeans.baseprocessor.BeanProcessorComponent;
import io.vavr.collection.Seq;
import static io.vavr.API.*;

@AutoService(javax.annotation.processing.Processor.class)
@SupportedAnnotationTypes(
  {
    "org.immutables.criteria.Criteria.Repository"
  }
)
public class ImmutablesCriteriaSpringProcessor extends BaseEnrichedBeansProcessor {

  @Override
  public Seq<BeanProcessorComponent> components(RoundEnvironment roundEnv) {
    return Seq();
  }

}
