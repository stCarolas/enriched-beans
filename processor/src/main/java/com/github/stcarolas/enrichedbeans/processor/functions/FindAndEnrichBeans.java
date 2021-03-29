package  com.github.stcarolas.enrichedbeans.processor.functions;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.inject.Inject;
import javax.inject.Named;

import com.github.stcarolas.enrichedbeans.processor.spec.CanProcessBeans;
import com.github.stcarolas.enrichedbeans.processor.spec.JavaClass;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.vavr.Function2;
import io.vavr.collection.Iterator;
import io.vavr.collection.List;
import io.vavr.collection.Seq;
import io.vavr.control.Try;

import static io.vavr.API.*;

@Named("FindAndEnrichBeans")
@SuppressWarnings("serial")
public class FindAndEnrichBeans
  implements Function2<RoundEnvironment,ProcessingEnvironment,Boolean> {

  public Boolean apply(
    RoundEnvironment roundEnv,
    ProcessingEnvironment processingEnv
  ){
    List<JavaClass> createdClasses = For(
      findBeans.apply(roundEnv),
      processors
    )
      .yield( (bean, processor) -> processor.apply(processingEnv, bean) )
      .toList();
    createdClasses.forEach(created -> log.info("created: {}", created.name()));
    List<Try<Void>> errors = createdClasses
      .map(created -> created.writeTo(processingEnv))
      .filter(Try::isFailure);
    errors.forEach(it -> log.info("error: {}",it));
    return errors.isEmpty();
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
  static final Logger log = LogManager.getLogger();
}
