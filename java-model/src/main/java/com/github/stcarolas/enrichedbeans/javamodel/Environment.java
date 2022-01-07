package com.github.stcarolas.enrichedbeans.javamodel;

import static io.vavr.API.Option;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.inject.Inject;
import javax.inject.Named;
import javax.lang.model.element.TypeElement;
import com.squareup.javapoet.TypeSpec;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.squareup.javapoet.JavaFile;
import io.vavr.Function2;
import io.vavr.collection.List;
import io.vavr.control.Option;
import io.vavr.control.Try;

@Named("Environment")
public class Environment {
  private static final Logger log = LogManager.getLogger();

  private RoundEnvironment roundEnv;
  private ProcessingEnvironment processingEnv;

  @Inject
  public Environment(RoundEnvironment roundEnv, ProcessingEnvironment processingEnv) {
    this.roundEnv = roundEnv;
    this.processingEnv = processingEnv;
  }

  public List<TypeElement> javaClasses() {
    return List.ofAll(roundEnv.getRootElements()).map(element -> (TypeElement) element);
  }

  public Option<String> getOption(String name){
    return Option(processingEnv.getOptions().get(name))
      .filter(it -> !it.isBlank());
  }

  // todo refack
  public Function2<String, TypeSpec, Try<Void>> writeSourceFileFn() {
    return (packageName, spec) -> Try.run(
      () -> JavaFile.builder(packageName, spec).build().writeTo(processingEnv.getFiler())
    )
      .onFailure(error -> log.error("Error while writing source file: {}", error));
  }
}
