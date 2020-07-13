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
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import com.github.stcarolas.enrichedbeans.annotations.Enrich;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import io.vavr.Function2;
import io.vavr.collection.List;
import io.vavr.collection.Seq;
import io.vavr.control.Try;

@AutoService(Processor.class)
@SupportedAnnotationTypes("com.github.stcarolas.enrichedbeans.annotations.Enrich")
public class EnrichProcessor extends AbstractProcessor {

    @Override
    public boolean process(
        java.util.Set<? extends TypeElement> annotations,
        RoundEnvironment roundEnv
    ) {
        Seq<Try<Void>> created = collectBeans(
            roundEnv.getElementsAnnotatedWith(Enrich.class)
        )
            .map(TargetBean::new)
            .map(factorySource)
            .map(source -> run(() -> source.writeTo(processingEnv.getFiler())));

        return !created.exists(Try::isFailure);
    }

    private Seq<TypeElement> collectBeans(
        java.util.Set<? extends Element> annotatedElements
    ) {
        return List.ofAll(annotatedElements)
            .map(element -> (TypeElement) element.getEnclosingElement())
            .distinct();
    }

    private JavaFile sourceFile(String packageName, TypeSpec javaClass) {
        return JavaFile.builder(packageName, javaClass).build();
    }

    private Function<TargetBean, JavaFile> factorySource = bean -> sourceFile(
        bean.packageName(),
        factory(bean)
    );

    private TypeSpec factory(TargetBean bean) {
        return new CreateAssistingFactory()
            .apply(
                ImmutableAssistingFactoryConfig.builder()
                    .factoryClassName(bean.name() + "Factory")
                    .factoryMethodName(detectFactoryMethodName())
                    .targetType(bean.type())
                    .visibility(detectVisibility())
                    .instanceFields(bean.allFields().reject(Field::isEnriched))
                    .injectingFields(bean.allFields().filter(Field::isEnriched))
                    .build()
            );
    }

    private String detectFactoryMethodName() {
        String methodName = processingEnv.getOptions().get("factoryMethodName");
        return methodName == null ? "from" : methodName;
    }

    private FactoryVisibility detectVisibility() {
        return "package".equals(processingEnv.getOptions().get("factoryVisibility"))
            ? FactoryVisibility.PACKAGE
            : FactoryVisibility.PUBLIC;
    }
}
