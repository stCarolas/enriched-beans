package com.github.stcarolas.enrichedbeans.processor.domain;

import static io.vavr.API.Option;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.inject.Named;
import javax.lang.model.element.TypeElement;
import com.squareup.javapoet.TypeSpec;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.squareup.javapoet.JavaFile;
import dagger.Module;
import dagger.Provides;
import io.vavr.Function2;
import io.vavr.collection.List;
import io.vavr.control.Option;
import io.vavr.control.Try;

@Module
public class Environment {
  private RoundEnvironment roundEnv;
  private ProcessingEnvironment processingEnv;

  public Environment(RoundEnvironment roundEnv, ProcessingEnvironment processingEnv) {
    this.roundEnv = roundEnv;
    this.processingEnv = processingEnv;
  }

  @Provides
  @Named("javaClasses")
  public List<TypeElement> javaClasses() {
    return List.ofAll(roundEnv.getRootElements()).map(element -> (TypeElement) element);
  }

  @Provides
  public RoundEnvironment roundEnv(){
    return roundEnv;
  }

  @Provides
  public ProcessingEnvironment processingEnv(){
    return processingEnv;
  }

  @Provides
  @Named("defaultFactoryMethodName")
  public Option<String> detectFactoryMethodName() {
    return Option(processingEnv.getOptions().get("factoryMethodName"))
      .filter(name -> !name.isBlank());
  }

  @Provides
  @Named("visibility")
  public Option<String> detectVisibility() {
    return Option(processingEnv.getOptions().get("factoryVisibility"))
      .filter(name -> !name.isBlank());
  }

  @Provides
  @Named("factoryClassNameSuffix")
  public Option<String> detectFactoryClassNameSuffix() {
    return Option(processingEnv.getOptions().get("factoryClassNameSuffix"))
      .filter(name -> !name.isBlank());
  }

  @Provides
  @Named("WriteSourceFileFn")
  public Function2<String, TypeSpec, Try<Void>> writeSourceFileFn() {
    return (packageName, spec) -> Try.run(
      () -> JavaFile.builder(packageName, spec).build().writeTo(processingEnv.getFiler())
    )
      .onFailure(error -> log.error("Error while writing source file: {}", error));
  }

  private static final Logger log = LogManager.getLogger();
}
