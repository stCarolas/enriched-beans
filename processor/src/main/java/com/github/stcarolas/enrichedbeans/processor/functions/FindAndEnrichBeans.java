package  com.github.stcarolas.enrichedbeans.processor.functions;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.inject.Inject;
import javax.inject.Named;

import com.github.stcarolas.enrichedbeans.processor.spec.CanProcessBeans;


import io.vavr.Function2;
import io.vavr.collection.Seq;
import io.vavr.control.Try;

import static io.vavr.API.*;

@Named("FindAndEnrichBeans")
public class FindAndEnrichBeans
  implements Function2<RoundEnvironment,ProcessingEnvironment,Boolean> {

  public Boolean apply(
    RoundEnvironment roundEnv,
    ProcessingEnvironment processingEnv
  ){
    return For(
      findBeans.apply(roundEnv),
      processors
    )
      .yield( (bean, processor) -> processor.apply(processingEnv, bean) )
      .map(created -> created.writeTo(processingEnv))
      .filter(Try::isFailure)
      .isEmpty();
  }

  @Inject public FindAndEnrichBeans(
    FindBeans findBeans,
    Seq<CanProcessBeans> processors
  ){
    this.findBeans = findBeans;
    this.processors = processors;
  }

  private final FindBeans findBeans;
  private final Seq<CanProcessBeans> processors;
}
