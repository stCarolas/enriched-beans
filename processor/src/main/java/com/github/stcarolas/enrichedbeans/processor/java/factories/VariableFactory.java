package  com.github.stcarolas.enrichedbeans.processor.java.factories;

import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;

import com.github.stcarolas.enrichedbeans.processor.java.Variable;
import com.github.stcarolas.enrichedbeans.processor.java.ImmutableProvidedField;
import com.github.stcarolas.enrichedbeans.processor.java.ImmutableVariable;
import com.github.stcarolas.enrichedbeans.processor.java.ProvidedField;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;

import io.vavr.collection.List;
import static io.vavr.API.*;

public class VariableFactory {

  private AnnotationFactory annotationFactory  = new AnnotationFactory();

  public Variable from(Element element){
    return from((VariableElement)element);
  }

  public Variable from(VariableElement element){
    return ImmutableVariable.builder()
      .name(element.getSimpleName().toString())
      .type(ParameterizedTypeName.get(element.asType()))
      .annotations(List.ofAll(element.getAnnotationMirrors()).map(annotationFactory::from))
      .modifiers(List(Modifier.PRIVATE))
      .build();
  }

  public ProvidedField provided(Variable field){
    return ImmutableProvidedField.builder()
      .name(field.name())
      .type(
        ParameterizedTypeName.get(
          ClassName.get("javax.inject", "Provider"),
          field.type()
        )
      )
      .modifiers(field.modifiers())
      .annotations(field.annotations())
      .build();
  }

}
