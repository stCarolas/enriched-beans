package io.github.stcarolas.enrichedbeans.assistedinject;

import static io.vavr.API.Seq;
import javax.inject.Named;
import javax.lang.model.element.Modifier;
import io.github.stcarolas.enrichedbeans.javamodel.bean.GeneratedBean;
import io.github.stcarolas.enrichedbeans.javamodel.method.Method;
import io.github.stcarolas.enrichedbeans.javamodel.method.constructor.Constructor;
import io.github.stcarolas.enrichedbeans.javamodel.variable.Variable;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeSpec.Builder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.immutables.value.Value.Derived;
import org.immutables.value.Value.Immutable;
import org.immutables.vavr.encodings.VavrEncodingEnabled;
import io.vavr.collection.Seq;

@Immutable
@VavrEncodingEnabled
public abstract class AssistingFactoryBean extends GeneratedBean implements WithAssistingFactoryBean {
  private static final Logger log = LogManager.getLogger();

  abstract Modifier visibility();

  abstract Method factoryMethod();

  abstract Constructor constructor();

  @Override
  public Seq<Method> methods() {
    return Seq(constructor(), factoryMethod());
  }

  @Override
  @Derived
  protected TypeSpec spec() {
    Builder spec = TypeSpec.classBuilder(className())
      .addAnnotation(AnnotationSpec.builder(Named.class).build())
      .addFields(fields().map(Variable::asFieldSpec));
    if (visibility() != Modifier.DEFAULT) {
      spec = spec.addModifiers(visibility());
    }
    spec = methods().map(Method::spec).foldLeft(spec, Builder::addMethod);
    return spec.build();
  }
}
