package  com.github.stcarolas.enrichedbeans.processor.java;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;

import org.immutables.value.Value.Default;
import org.immutables.value.Value.Immutable;

import io.vavr.collection.Map;
import static io.vavr.API.*;

@Immutable public interface Annotation {

  String className();
  String packageName();

  @Default default Map<String, Object> parameters(){
    return Map();
  }

  default boolean is(Class<?> aClass){
    return (packageName() + "." + className())
      .equals(aClass.getCanonicalName());
  }

  default AnnotationSpec spec(){
    return parameters().foldLeft(
        AnnotationSpec.builder(ClassName.get(packageName(),className())),
        (builder, parameter) -> builder.addMember(
          parameter._1.substring(0, parameter._1.length()-2),
          String.format("\"%s\"", parameter._2.toString())
        )
      )
      .build();
  }

}
