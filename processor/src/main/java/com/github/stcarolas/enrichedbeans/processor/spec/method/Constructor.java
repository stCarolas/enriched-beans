package  com.github.stcarolas.enrichedbeans.processor.spec.method;

import static org.immutables.value.Value.Immutable;

import javax.inject.Inject;

import com.github.stcarolas.enrichedbeans.processor.java.Annotation;
import com.github.stcarolas.enrichedbeans.processor.java.Variable;
import com.github.stcarolas.enrichedbeans.processor.spec.HasAnnotation;
import com.github.stcarolas.enrichedbeans.processor.spec.HasName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;

import io.vavr.collection.Seq;

@Immutable public abstract class Constructor 
  implements Method, HasAnnotation { 

  abstract Seq<Variable> classFields();

  public MethodSpec spec(){
    return MethodSpec.constructorBuilder()
      .addParameters(classFields().map(Variable::asParameterSpec))
      .addAnnotations(annotations().map(Annotation::spec))
      .addCode(code())
      .build();
  };

  private CodeBlock code(){
    return classFields().foldLeft(
      CodeBlock.builder(), 
      (builder, field) -> builder.add("this.$N = $N;", field.name(), field.name())
    ).build();
  }

}
