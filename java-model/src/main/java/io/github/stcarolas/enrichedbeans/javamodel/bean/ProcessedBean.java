package io.github.stcarolas.enrichedbeans.javamodel.bean;

import javax.lang.model.element.TypeElement;

import com.squareup.javapoet.TypeName;

import org.immutables.value.Value.Derived;
import org.immutables.value.Value.Immutable;
import org.immutables.vavr.encodings.VavrEncodingEnabled;

import io.vavr.control.Try;

import static io.vavr.API.Success;

@Immutable
@VavrEncodingEnabled
public abstract class ProcessedBean extends Bean {

  public abstract TypeElement type();

  public Try<TypeName> typeName(){
    return Success(TypeName.get(type().asType()));
  }

  @Derived
  public String packageName() {
    String fullName = type().getQualifiedName().toString();
    String packageName = "";
    int lastDot = fullName.lastIndexOf('.');
    if (lastDot > 0) {
      packageName = fullName.substring(0, lastDot);
    }
    return packageName;
  }

  @Derived
  public String className() {
    return type().getSimpleName().toString();
  }

}
