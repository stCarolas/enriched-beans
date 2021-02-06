package com.github.stcarolas.enrichedbeans.processor;

import static com.squareup.javapoet.TypeSpec.classBuilder;
import java.util.function.Function;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.lang.model.element.Modifier;
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
import com.github.stcarolas.enrichedbeans.processor.spec.method.Constructor;
import com.github.stcarolas.enrichedbeans.processor.spec.method.FactoryMethod;
import com.github.stcarolas.enrichedbeans.processor.spec.method.HasMethodSpec;
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
import org.immutables.value.Value.Default;
import org.immutables.value.Value.Derived;
import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Style;

import io.vavr.Function2;
import io.vavr.Function4;
import io.vavr.collection.Seq;
import io.vavr.control.Option;
import static io.vavr.API.*;

@Style(overshadowImplementation=true)
@Immutable public abstract class AssistingFactory 
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

    public AssistingFactory withMethod(Option<HasMethodSpec> method){
      return method
        .map(methods()::append)
        .map(ImmutableAssistingFactory.builder().from(this)::methods)
        .map(ImmutableAssistingFactory.Builder::build)
        .getOrElse(this);
    }
}
