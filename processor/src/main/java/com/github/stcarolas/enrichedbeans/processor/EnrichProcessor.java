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
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class EnrichProcessor extends AbstractProcessor {

    @Override
    public boolean process(
        java.util.Set<? extends TypeElement> annotations,
        RoundEnvironment roundEnv
    ) {
        Seq<Try<Void>> created = collectBeans(
            roundEnv.getElementsAnnotatedWith(Enrich.class)
        )
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

    private Seq<Field> enrichedFieldsOf(TypeElement type) {
        return allFieldsOf(type).filter(Field::isEnriched);
    }

    private Seq<Field> notEnrichedFieldsOf(TypeElement type) {
        return allFieldsOf(type).reject(Field::isEnriched);
    }

    private Seq<Field> allFieldsOf(TypeElement type) {
        return List.ofAll(type.getEnclosedElements())
            .filter(element -> element.getKind().isField())
            .map(element -> Field.from((VariableElement) element));
    }

    private JavaFile sourceFile(String packageName, TypeSpec javaClass) {
        return JavaFile.builder(packageName, javaClass).build();
    }

    private Function<TypeElement, JavaFile> factorySource = bean -> sourceFile(
        packageName(bean),
        factory(bean)
    );

    private TypeSpec factory(TypeElement bean) {
        return new CreateAssistingFactory()
            .apply(
                bean.getSimpleName().toString() + "Factory",
                TypeName.get(bean.asType()),
                enrichedFieldsOf(bean),
                notEnrichedFieldsOf(bean)
            );
    }

    private String packageName(TypeElement type) {
        String fullName = type.getQualifiedName().toString();
        String packageName = "";
        int lastDot = fullName.lastIndexOf('.');
        if (lastDot > 0) {
            packageName = fullName.substring(0, lastDot);
        }
        return packageName;
    }
}
