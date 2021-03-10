package com.github.stcarolas.enrichedbeans.processor.beans.assistingfactory;

import static io.vavr.API.Seq;

import javax.inject.Named;

import com.github.stcarolas.enrichedbeans.processor.java.Bean;
import com.github.stcarolas.enrichedbeans.processor.java.ImmutableAnnotation;
import com.github.stcarolas.enrichedbeans.processor.java.Variable;
import com.github.stcarolas.enrichedbeans.processor.spec.HasVisibility;
import com.github.stcarolas.enrichedbeans.processor.spec.JavaClass;
import com.github.stcarolas.enrichedbeans.processor.spec.method.Constructor;
import com.github.stcarolas.enrichedbeans.processor.spec.method.FactoryMethod;
import com.github.stcarolas.enrichedbeans.processor.spec.method.HasMethodSpec;
import com.github.stcarolas.enrichedbeans.processor.spec.method.ImmutableConstructor;
import com.github.stcarolas.enrichedbeans.processor.spec.method.ImmutableFactoryMethod;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeSpec.Builder;

import org.immutables.value.Value.Default;
import org.immutables.value.Value.Derived;
import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Style;

import io.vavr.collection.Seq;
import io.vavr.control.Option;

@Style(overshadowImplementation=true)
@Immutable public abstract class AssistingFactoryBean 
  implements JavaClass, HasVisibility {

    abstract Bean targetBean();

    @Derived public String packageName(){
      return targetBean().packageName();
    }

    @Derived public String name(){
      return targetBean().name() + "Factory";
    }

    @Derived public FactoryMethod factoryMethod(){
      return ImmutableFactoryMethod.builder()
        .name("from")
        .returnType(targetBean().type())
        .parameters(targetBean().fields().reject(Variable::isEnriched))
        .objectFields(targetBean().fields().filter(Variable::isEnriched))
        .build();
    }

    @Derived public Constructor constructor(){
      return ImmutableConstructor.builder()
        .classFields(targetBean().fields().filter(Variable::isEnriched))
        .annotations(
          Seq(
            ImmutableAnnotation.builder()
              .className("Inject")
              .packageName("javax.inject")
              .build()
          )
        )
        .build();
    }

    @Default public Seq<HasMethodSpec> methods(){
      return Seq(constructor(), factoryMethod());
    }

    @Derived public TypeSpec spec(){
      Builder spec = TypeSpec.classBuilder(name())
        .addAnnotation(AnnotationSpec.builder(Named.class).build())
        .addFields(
          targetBean().fields().filter(Variable::isEnriched)
            .map(Variable::asFieldSpec)
            .toJavaList()
        );
      spec = methods().foldLeft(
        spec,
        (specBuilder,method) -> specBuilder.addMethod(method.spec())
      );
      return spec.build();
    }

    public AssistingFactoryBean withMethod(Option<HasMethodSpec> method){
      return method
        .map(methods()::append)
        .map(it -> ImmutableAssistingFactoryBean.builder().from(this).methods(it).build())
        .getOrElse(this);
    }
}
