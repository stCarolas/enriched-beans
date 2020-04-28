package com.github.stcarolas.enrichedbeans.processor;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.JavaFileObject;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.MethodSpec.Builder;

import io.vavr.collection.HashSet;
import io.vavr.collection.Seq;
import io.vavr.collection.Set;
import com.github.stcarolas.enrichedbeans.annotations.Enrich;
import static io.vavr.API.*;

@AutoService(Processor.class)
@SupportedAnnotationTypes("com.github.stcarolas.enrichedbeans.annotations.Enrich")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class EnrichProcessor extends AbstractProcessor  {

	@Override
	public boolean process(
    java.util.Set<? extends TypeElement> annotations, 
    RoundEnvironment roundEnv
  ){
    java.util.Set<? extends Element> annotatedElements 
      = roundEnv.getElementsAnnotatedWith(Enrich.class);
    Set<TypeElement> classes = HashSet.empty();
    for (var element: annotatedElements){
      classes = classes.add((TypeElement) element.getEnclosingElement());
    }
    classes.forEach(this::handle);
 
    return true;
	}

  private void handle(TypeElement type){
    try {
      String fullName = type.getQualifiedName().toString();
      String packageName = packageName(fullName);
      String className = type.getSimpleName().toString();

      MethodSpec.Builder constructor = MethodSpec.constructorBuilder();
      MethodSpec.Builder createMethod = MethodSpec.methodBuilder("from")
        .returns(TypeName.get(type.asType()))
        .addModifiers(Modifier.PUBLIC);
      TypeSpec.Builder factory = TypeSpec.classBuilder(className + "Factory");
      List<? extends Element> enclosedList = type.getEnclosedElements();
      enclosedList.forEach( 
        child -> {
          if (child.getKind().isField()){
            String fieldName = child.getSimpleName().toString();
            TypeMirror childType = ((VariableElement) child).asType();
            var parameter = ParameterSpec
              .builder(ParameterizedTypeName.get(childType), fieldName);
            boolean isEnriched = false;
            for (AnnotationMirror annotation: child.getAnnotationMirrors()){
              Map<? extends ExecutableElement, ? extends AnnotationValue> values = annotation.getElementValues();
              TypeElement annoElement = ((TypeElement)annotation.getAnnotationType().asElement());
              String name = annoElement.getQualifiedName().toString();
              if ("com.github.stcarolas.enrichedbeans.annotations.Enrich".equals(name)){
                isEnriched = true;
              } else {
                parameter.addAnnotation(AnnotationSpec.get(annotation));
              }
            }
            if (isEnriched){
              constructor.addParameter(parameter.build());
              constructor.addStatement("this.$N = $N", fieldName, fieldName);
              factory.addField(
                ParameterizedTypeName.get(childType), 
                fieldName,
                Modifier.PRIVATE
              );
            } else {
              createMethod.addParameter(
                ParameterizedTypeName.get(childType), 
                child.getSimpleName().toString());
            }
          }
        }
      );
      Seq<String> fields = Seq();
      for (var child: enclosedList){
          if (child.getKind().isField()){
            fields = fields.append(child.getSimpleName().toString());
          }
      }
      String argLine = fields.intersperse(",").fold("", (a,b) -> a+b);
      String code = "return new " + fullName +"(" + argLine + ")";
      createMethod.addStatement(code);
      
      factory
          .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
          .addMethod(constructor.build())
          .addMethod(createMethod.build())
          .addAnnotation(Singleton.class)
          .addAnnotation(Named.class);
      
      JavaFile javaFile = JavaFile.builder(packageName, factory.build())
        .build();
     
      javaFile.writeTo(processingEnv.getFiler());
    } catch(Exception e ){}
  }

  private String packageName(String fullName){
    String packageName = "";
    int lastDot = fullName.lastIndexOf('.');
    if (lastDot > 0) {
        packageName = fullName.substring(0, lastDot);
    }
    return packageName;
  }

}
