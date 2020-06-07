package com.github.stcarolas.enrichedbeans.processor;

import static com.squareup.javapoet.TypeSpec.classBuilder;
import static io.vavr.control.Try.run;
import java.util.function.Function;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import com.github.stcarolas.enrichedbeans.annotations.Enrich;
import com.github.stcarolas.enrichedbeans.annotations.Implement;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.MethodSpec.Builder;
import io.vavr.Function2;
import io.vavr.collection.List;
import io.vavr.collection.Seq;
import io.vavr.control.Try;
import static io.vavr.API.*;

@AutoService(Processor.class)
@SupportedAnnotationTypes("com.github.stcarolas.enrichedbeans.annotations.Implement")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class ImplementProcessor extends AbstractProcessor {

    @Override
    public boolean process(
        java.util.Set<? extends TypeElement> annotations,
        RoundEnvironment roundEnv
    ) {
        println("search for implement");
        List<TargetBean> targets = collectBeans(
            roundEnv.getElementsAnnotatedWith(Implement.class)
        )
            .map(bean -> bean.getEnclosingElement())
            .map(TargetBean::new);
        List<TypeSpec> implementations = targets.map(
            bean -> bean.allSubtypes()
                .map(type -> new TargetBean(type))
                .flatMap(
                    subtype -> subtype.allMethods()
                        .map(method -> functionMethod(bean, method))
                )
                .foldLeft(
                    bean.newEmptyBean().toBuilder(),
                    (beanImplementation, method) -> beanImplementation.addMethod(method)
                )
                .addModifiers(Modifier.PUBLIC)
                .build()
        );
        implementations.forEach(t -> println(t));
        return true;
    }

    public FieldSpec functionField(TargetBean bean, ExecutableElement method) {
        List<Object> types = List.ofAll(method.getParameters())
          .map($ -> $.asType());
        return null;
    }

    public MethodSpec functionMethod(TargetBean bean, ExecutableElement method) {
        println("create method: " + method.getSimpleName().toString());
        ParameterizedTypeName functinoType = ParameterizedTypeName.get(
            ClassName.get(Function.class),
            ClassName.get(String.class),
            ClassName.get(String.class)
        );
        println(functinoType);
        List<? extends VariableElement> knownParameters = List.ofAll(
            method.getParameters()
        )
            .filter(
                param -> bean.allFields()
                    .exists(
                        field -> field.name().equals(param.getSimpleName().toString())
                    )
            );
        List<? extends VariableElement> unknownParameters = List.ofAll(
            method.getParameters()
        )
            .reject(
                param -> bean.allFields()
                    .exists(
                        field -> field.name().equals(param.getSimpleName().toString())
                    )
            );
        Builder function = MethodSpec.methodBuilder(method.getSimpleName().toString())
            .addModifiers(Modifier.PUBLIC)
            .returns(TypeName.get(method.getReturnType()));
        println(function.build().toString());
        return function.build();
    }

    public static Function<TargetBean, TypeSpec> constructBean = TargetBean::newEmptyBean;

    private List<? extends Element> collectBeans(
        java.util.Set<? extends Element> annotatedElements
    ) {
        return List.ofAll(annotatedElements).distinct();
    }
}
