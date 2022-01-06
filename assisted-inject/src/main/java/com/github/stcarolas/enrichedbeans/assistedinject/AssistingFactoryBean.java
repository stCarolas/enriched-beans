package com.github.stcarolas.enrichedbeans.assistedinject;

import static io.vavr.API.Seq;
import javax.inject.Named;
import javax.lang.model.element.Modifier;
import com.github.stcarolas.enrichedbeans.javamodel.SourceFile;
import com.github.stcarolas.enrichedbeans.javamodel.method.Method;
import com.github.stcarolas.enrichedbeans.javamodel.method.constructor.Constructor;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeSpec.Builder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.immutables.value.Value.Default;
import org.immutables.value.Value.Derived;
import org.immutables.value.Value.Immutable;
import io.vavr.Function2;
import io.vavr.collection.Seq;
import io.vavr.control.Try;

@Immutable
public abstract class AssistingFactoryBean implements SourceFile {
  private static final Logger log = LogManager.getLogger();

  abstract Modifier visibility();

  abstract Function2<String, TypeSpec, Try<Void>> writeSourceFileFn();

  abstract String packageName();

  abstract String className();

  abstract Method factoryMethod();

  abstract Constructor constructor();

  abstract Seq<FieldSpec> fields();

  @Default
  Seq<Method> methods() {
    return Seq(constructor(), factoryMethod());
  }

  @Derived
  protected TypeSpec spec() {
    Builder spec = TypeSpec.classBuilder(className())
      .addAnnotation(AnnotationSpec.builder(Named.class).build())
      .addFields(fields());
    if (visibility() != Modifier.DEFAULT) {
      spec = spec.addModifiers(visibility());
    }
    spec = methods().map(Method::spec).foldLeft(spec, Builder::addMethod);
    return spec.build();
  }

  public Try<SourceFile> write() {
    return writeSourceFileFn().apply(packageName(), spec()).map(result -> this);
  }
}
