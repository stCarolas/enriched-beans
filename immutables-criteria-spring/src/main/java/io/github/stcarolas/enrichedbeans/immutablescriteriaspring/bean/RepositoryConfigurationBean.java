package io.github.stcarolas.enrichedbeans.immutablescriteriaspring.bean;

import static io.vavr.API.Seq;

import com.squareup.javapoet.CodeBlock;

import io.github.stcarolas.enrichedbeans.javamodel.annotation.Annotation;
import io.github.stcarolas.enrichedbeans.javamodel.annotation.ImmutableAnnotationImpl;
import io.github.stcarolas.enrichedbeans.javamodel.bean.GeneratedBean;
import io.github.stcarolas.enrichedbeans.javamodel.method.ImmutableMethodImpl;
import io.github.stcarolas.enrichedbeans.javamodel.method.Method;
import io.vavr.collection.Seq;

import org.immutables.value.Value.Derived;
import org.immutables.value.Value.Immutable;
import org.immutables.vavr.encodings.VavrEncodingEnabled;

@Immutable
@VavrEncodingEnabled
public abstract class RepositoryConfigurationBean extends GeneratedBean {

  abstract String entityName();

  @Derived
  public String className(){
    return entityName() + "RepositoryConfiguration";

  }

  @Override
  public Seq<Annotation> annotations() {
    return Seq(
      ImmutableAnnotationImpl
        .builder()
        .className("Configuration")
        .packageName("org.springframework.context.annotation")
        .build()
    );
  }

  @Override
  public Seq<Method> methods() {
    ImmutableMethodImpl.builder()
      .name(entityName() + "Repository")
      .addAnnotations(
        ImmutableAnnotationImpl
          .builder()
          .className("Bean")
          .packageName("org.springframework.context.annotation")
          .build()
      )
        .code(
          CodeBlock.builder()
            .addStatement(
              String.format(
                "return new %sRepository();",
                entityName()
              )
            )
            .build()
        )
      .build();
    return Seq();
  }
}
