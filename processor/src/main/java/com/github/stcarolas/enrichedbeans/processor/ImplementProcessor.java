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
import javax.lang.model.type.TypeMirror;
import com.github.stcarolas.enrichedbeans.annotations.Enrich;
import com.github.stcarolas.enrichedbeans.annotations.Implement;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
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
        List<Element> originalTargets = collectBeans(
            roundEnv.getElementsAnnotatedWith(Implement.class)
        )
            .map(Element::getEnclosingElement);
        List<TypeMirror> types = originalTargets.map(
            element -> ((TypeElement) element).asType()
        );
        List<TargetBean> targets = originalTargets.map(TargetBean::new);
        List<Seq<ExecutableElement>> originalMethods = targets.map(
            bean -> bean.allSubtypes()
                .map(TargetBean::new)
                .flatMap(TargetBean::allMethods)
        );
        originalMethods.forEach(method -> println("original: " + method));
        List<Seq<FieldSpec>> fieldsForTargets = targets.zipWith(
            originalMethods,
            (target, targetMethods) -> targetMethods.map(
                method -> functionField(target, method)
            )
        );
        fieldsForTargets.forEach(field -> println("fields: " + field));
        List<Seq<MethodSpec>> methodsForTargets = targets.zipWith(
            originalMethods,
            (target, targetMethods) -> targetMethods.map(
                method -> functionMethod(target, method)
            )
        );
        methodsForTargets.forEach(method -> println("methods: " + method));
        List<TypeSpec> implementations = targets.zipWith(
            methodsForTargets,
            (target, methods) -> methods.foldLeft(
                target.newEmptyBean().toBuilder(),
                (enrichedBean, method) -> enrichedBean.addMethod(method)
            )
                .build()
        )
            .zipWith(
                fieldsForTargets,
                (target, fields) -> fields.foldLeft(
                    target.toBuilder(),
                    (enrichedBean, field) -> enrichedBean.addField(field)
                )
                    .build()
            )
            .map(bean -> bean.toBuilder().addModifiers(Modifier.PUBLIC).build());
        List<MethodSpec> constructors = implementations.zipWith(
            fieldsForTargets,
            this::constructor
        );

        List<Seq<Field>> injectingFields = fieldsForTargets.map(
            fields -> fields.map($ -> Field.from($.name, $.type, List()))
        );

        List<TypeSpec> factories = targets.zipWith(
            injectingFields,
            (bean, fields) -> new CreateAssistingFactory()
                .apply(bean.name() + "Factory", bean.type(), fields, List())
        );
        factories.forEach(f -> println(f));
        implementations =
            implementations.map(TypeSpec::toBuilder)
                .zipWith(constructors, TypeSpec.Builder::addMethod)
                .map(TypeSpec.Builder::build);
        implementations.forEach(t -> println(t));
        return true;
    }

    public MethodSpec constructor(TypeSpec bean, Seq<FieldSpec> fields) {
        Builder constructor = MethodSpec.constructorBuilder()
            .setName(bean.name)
            .addModifiers(Modifier.PUBLIC);
        constructor =
            fields.map(field -> ParameterSpec.builder(field.type, field.name).build())
                .foldLeft(constructor, MethodSpec.Builder::addParameter);
        constructor =
            fields.map(field -> String.format("this.%s = %s", field.name, field.name))
                .foldLeft(constructor, MethodSpec.Builder::addCode);
        return constructor.build();
    }

    public void createFactory() {}

    public FieldSpec functionField(TargetBean bean, ExecutableElement method) {
        List<Object> types = List.ofAll(method.getParameters()).map($ -> $.asType());
        TypeName[] parameters = List.ofAll(method.getParameters())
            .map(param -> param.asType())
            .map($ -> ClassName.get($))
            .append(ClassName.get(method.getReturnType()))
            .toJavaArray(TypeName.class);
        ParameterizedTypeName functionType = ParameterizedTypeName.get(
            ClassName.get(Function.class),
            parameters
        );
        println("calculated filed type: " + functionType);
        return FieldSpec.builder(functionType, method.getSimpleName().toString())
            .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
            .build();
    }

    public MethodSpec functionMethod(TargetBean bean, ExecutableElement method) {
        println("create method: " + method.getSimpleName().toString());
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
        String returnLine = List.ofAll(method.getParameters())
            .map(param -> param.getSimpleName().toString())
            .mkString(
                String.format("return %s.apply(", method.getSimpleName()),
                ",",
                ");"
            );
        Builder function = MethodSpec.methodBuilder(method.getSimpleName().toString())
            .addModifiers(Modifier.PUBLIC)
            .addCode(returnLine)
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
