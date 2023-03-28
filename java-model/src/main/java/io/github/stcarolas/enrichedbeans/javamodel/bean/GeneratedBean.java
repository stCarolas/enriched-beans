package io.github.stcarolas.enrichedbeans.javamodel.bean;

import com.squareup.javapoet.TypeSpec;
import io.github.stcarolas.enrichedbeans.javamodel.annotation.Annotation;
import io.github.stcarolas.enrichedbeans.javamodel.method.Method;
import io.github.stcarolas.enrichedbeans.javamodel.variable.Variable;
import io.vavr.control.Try;
import org.immutables.value.Value.Derived;
import org.immutables.value.Value.Immutable;
import org.immutables.vavr.encodings.VavrEncodingEnabled;

public abstract class GeneratedBean extends Bean {

  @Derived
  protected TypeSpec spec() {
    return TypeSpec
      .classBuilder(className())
      .addAnnotations(annotations().map(Annotation::spec))
      .addFields(fields().map(Variable::asFieldSpec))
      .addMethods(methods().map(Method::spec))
      .build();
  }

  public Try<GeneratedBean> write() {
    return env().writeSource(packageName(), spec()).map(it -> this);
  }

  @Immutable
  @VavrEncodingEnabled
  public abstract static class GeneratedBeanImpl extends GeneratedBean {}
}
