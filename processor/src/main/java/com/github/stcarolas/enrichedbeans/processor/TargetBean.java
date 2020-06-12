package  com.github.stcarolas.enrichedbeans.processor;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import static com.squareup.javapoet.TypeSpec.classBuilder;

import io.vavr.collection.List;
import io.vavr.collection.Seq;
import lombok.RequiredArgsConstructor;
import static io.vavr.API.*;

@RequiredArgsConstructor
public class TargetBean {
  private final Element original;

  public TypeSpec newEmptyBean(){
    return classBuilder(original.getSimpleName().toString()).build();
  }

  public TypeName type(){
    return TypeName.get(original.asType());
  }

  public String name(){
    return original.getSimpleName().toString();
  }

  public Seq<TypeElement> allSubtypes(){
    return List.ofAll(original.getEnclosedElements())
      .filter(element -> element.getKind().isInterface())
      .map(element -> (TypeElement)element);
  }

  public Seq<Field> allFields(){
    return List.ofAll(original.getEnclosedElements())
      .filter(element -> element.getKind().isField())
      .map(element -> Field.from((VariableElement)element));
  }

  public Seq<ExecutableElement> allMethods(){
    return List.ofAll(original.getEnclosedElements())
      .filter(element -> element.getKind().equals(ElementKind.METHOD))
      .map( $ -> (ExecutableElement)$)
      ;
  }

}
