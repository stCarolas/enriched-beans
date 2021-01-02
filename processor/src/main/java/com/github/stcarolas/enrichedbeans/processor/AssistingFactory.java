package com.github.stcarolas.enrichedbeans.processor;

import static com.squareup.javapoet.TypeSpec.classBuilder;
import java.util.function.Function;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.lang.model.element.Modifier;
import com.github.stcarolas.enrichedbeans.processor.functions.CreateFactoryMethod;
import com.github.stcarolas.enrichedbeans.processor.java.Annotation;
import com.github.stcarolas.enrichedbeans.processor.java.Bean;
import com.github.stcarolas.enrichedbeans.processor.java.ImmutableAnnotation;
import com.github.stcarolas.enrichedbeans.processor.java.Variable;
import com.github.stcarolas.enrichedbeans.processor.java.VariableFactory;
import com.github.stcarolas.enrichedbeans.processor.spec.HasAnnotation;
import com.github.stcarolas.enrichedbeans.processor.spec.HasFields;
import com.github.stcarolas.enrichedbeans.processor.spec.HasName;
import com.github.stcarolas.enrichedbeans.processor.spec.HasPackage;
import com.github.stcarolas.enrichedbeans.processor.spec.HasVisibility;
import com.github.stcarolas.enrichedbeans.processor.spec.JavaClass;
import com.github.stcarolas.enrichedbeans.processor.spec.method.ImmutableConstructor;
import com.github.stcarolas.enrichedbeans.processor.spec.method.ImmutableFactoryMethod;
import com.github.stcarolas.enrichedbeans.processor.spec.method.Method;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeSpec.Builder;
import com.squareup.javapoet.ClassName;
import org.immutables.value.Value;
import org.immutables.value.Value.Derived;
import org.immutables.value.Value.Immutable;

import io.vavr.Function2;
import io.vavr.Function4;
import io.vavr.collection.Seq;
import io.vavr.control.Option;
import static io.vavr.API.*;

@Immutable public abstract class AssistingFactory 
  implements JavaClass, HasVisibility {

    abstract Bean targetBean();

    @Derived public String packageName(){
      return targetBean().packageName();
    }

    @Derived String name(){
      return targetBean().name() + "Factory";
    }

    @Derived Seq<Variable> injectedFields(){
      return targetBean().fields().filter(Variable::isEnriched);
    }

    @Derived Seq<Variable> nonInjectedFields(){
      return targetBean().fields().reject(Variable::isEnriched);
    }

    public TypeSpec spec(){
      return TypeSpec.classBuilder(name())
        .addFields(injectedFields().map(Variable::asFieldSpec).toJavaList())
        .addMethod(
          ImmutableConstructor.builder()
            .classFields(injectedFields())
            .annotations(
              Seq(
                ImmutableAnnotation.builder()
                  .className("Inject")
                  .packageName("javax.inject")
                  .build()
              )
            )
            .build()
            .spec()
        )
        .addMethod(
          ImmutableFactoryMethod.builder()
            .name("from")
            .returnType(targetBean().type())
            .signatureFields(nonInjectedFields())
            .objectFields(injectedFields())
            .build()
            .spec()
        )
        .build();
    }

}
