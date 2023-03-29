package io.github.stcarolas.enrichedbeans.baseprocessor;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import io.github.stcarolas.enrichedbeans.javamodel.bean.GeneratedBean;
import io.vavr.collection.Seq;
import io.vavr.control.Try;

public abstract class BaseEnrichedBeansProcessor extends AbstractProcessor {
  private static final Logger log = LogManager.getLogger(BaseEnrichedBeansProcessor.class);

  abstract public Seq<BeanProcessorComponent> components(
    RoundEnvironment roundEnvironment
  );

  @Override
  public boolean process(
    java.util.Set<? extends TypeElement> types,
    RoundEnvironment roundEnv
  ) {
    log.debug("Processing beans");
    Seq<ProcessingResult> generationResults = components(roundEnv)
      .map(component -> component.process());
    Seq<Try<Seq<GeneratedBean>>> beans = generationResults.flatMap(
      result -> result.beans()
    );
    beans.forEach(
      generationTry -> generationTry.map(beansSeq -> beansSeq.map(GeneratedBean::write))
    );
    beans.filter(Try::isFailure).forEach(
      error -> log.error(
        "Exception while processing beans: {}",
        // error.getCause().getMessage()
        error
      )
    );
    Boolean result = generationResults.foldLeft(
      true,
      (overallResult, componentResult) -> overallResult && componentResult.isFinished()
    );
    log.debug("Finished bean processing");
    return result;
  }
}
