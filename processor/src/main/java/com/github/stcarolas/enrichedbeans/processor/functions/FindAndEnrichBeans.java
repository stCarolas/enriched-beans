package com.github.stcarolas.enrichedbeans.processor.functions;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.inject.Inject;
import javax.inject.Named;
import com.github.stcarolas.enrichedbeans.processor.spec.CanProcessBeans;
import com.github.stcarolas.enrichedbeans.processor.spec.JavaClass;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import io.vavr.collection.List;
import io.vavr.collection.Seq;
import io.vavr.control.Try;
import static io.vavr.API.*;

@Named("FindAndEnrichBeans")
public class FindAndEnrichBeans {

  @Inject
  public FindAndEnrichBeans(FindBeans findBeans, Seq<CanProcessBeans> processors) {
    this.findBeans = findBeans;
    this.processors = processors;
  }

  public Boolean apply(RoundEnvironment roundEnv, ProcessingEnvironment processingEnv) {
    List<JavaClass> createdClasses = For(processors, findBeans.apply(roundEnv))
      .yield(CanProcessBeans::apply)
      .toList();
    createdClasses.forEach(created -> log.info("created: {}", created.name()));
    createdClasses.map(created -> created.writeTo(processingEnv))
      .filter(Try::isFailure)
      .forEach(it -> log.error("error: {}", it));
    return true;
  }

  private final FindBeans findBeans;
  private final Seq<CanProcessBeans> processors;

  private static final Logger log = LogManager.getLogger();
}
