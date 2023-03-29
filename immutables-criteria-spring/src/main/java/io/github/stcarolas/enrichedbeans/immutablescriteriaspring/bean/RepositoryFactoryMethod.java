package io.github.stcarolas.enrichedbeans.immutablescriteriaspring.bean;

import static io.vavr.API.*;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.TypeName;
import io.github.stcarolas.enrichedbeans.javamodel.annotation.Annotation;
import io.github.stcarolas.enrichedbeans.javamodel.annotation.ImmutableAnnotationImpl;
import io.github.stcarolas.enrichedbeans.javamodel.method.Method;
import io.github.stcarolas.enrichedbeans.javamodel.variable.ImmutableVariableImpl;
import io.github.stcarolas.enrichedbeans.javamodel.variable.Variable;
import io.vavr.collection.Seq;
import org.immutables.value.Value;
import org.immutables.vavr.encodings.VavrEncodingEnabled;

@Value.Immutable
@VavrEncodingEnabled
public abstract class RepositoryFactoryMethod extends Method {

  public abstract String entityClassName();

  public abstract String entityPackageName();

  @Override
  public String name() {
    return entityClassName() + "Repository";
  }

  @Override
  public Seq<Variable> parameters() {
    return Seq(
      ImmutableVariableImpl
        .builder()
        .name("backend")
        .typeName(ClassName.get("org.immutables.criteria.backend", "Backend"))
        .build()
    );
  }

  @Override
  public Seq<Annotation> annotations() {
    return Seq(
      ImmutableAnnotationImpl
        .builder()
        .className("Bean")
        .packageName("org.springframework.context.annotation")
        .build()
    );
  }

  @Override
  public TypeName returnType() {
    return ClassName.get(entityPackageName(), entityClassName() + "Repository");
  }

  @Override
  public CodeBlock code() {
    return CodeBlock
      .builder()
      .addStatement(
        String.format("return new %sRepository(backend)", entityClassName())
      )
      .build();
  }
}
