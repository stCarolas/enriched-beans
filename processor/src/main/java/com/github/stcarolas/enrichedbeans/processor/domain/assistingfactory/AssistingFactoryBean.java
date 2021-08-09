package com.github.stcarolas.enrichedbeans.processor.domain.assistingfactory;

import javax.inject.Named;
import javax.lang.model.element.Modifier;
import com.github.stcarolas.enrichedbeans.processor.domain.SourceFile;
import com.github.stcarolas.enrichedbeans.processor.spec.method.Constructor;
import com.github.stcarolas.enrichedbeans.processor.spec.method.api.HasMethodSpec;
import com.github.stcarolas.enrichedbeans.processor.spec.method.api.MethodWithSpec;
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

import static io.vavr.API.*;

@Immutable
public abstract class AssistingFactoryBean implements SourceFile {

  abstract Modifier visibility();

  abstract Function2<String, TypeSpec, Try<Void>> writeSourceFileFn();

  abstract String packageName();

  abstract String name();

  abstract MethodWithSpec factoryMethod();

  abstract Constructor constructor();

  abstract Seq<FieldSpec> fields();

  @Default
  Seq<HasMethodSpec> methods() {
    return Seq(constructor(), factoryMethod());
  }

  @Derived
  protected TypeSpec spec() {
    Builder spec = TypeSpec.classBuilder(name())
      .addAnnotation(AnnotationSpec.builder(Named.class).build())
      .addFields(fields());
    if (visibility() != Modifier.DEFAULT) {
      spec = spec.addModifiers(visibility());
    }
    spec = methods().map(HasMethodSpec::spec).foldLeft(spec, Builder::addMethod);
    return spec.build();
  }

  public Try<Void> write() {
    return writeSourceFileFn().apply(packageName(), spec());
  }

  static final Logger log = LogManager.getLogger();
}
