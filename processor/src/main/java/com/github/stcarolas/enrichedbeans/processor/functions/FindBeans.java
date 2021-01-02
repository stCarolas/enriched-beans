package com.github.stcarolas.enrichedbeans.processor.functions;

import javax.annotation.processing.RoundEnvironment;
import javax.inject.Inject;
import javax.inject.Named;
import javax.lang.model.element.TypeElement;

import com.github.stcarolas.enrichedbeans.annotations.Enrich;
import com.github.stcarolas.enrichedbeans.processor.java.Bean;

import io.vavr.Function1;
import io.vavr.collection.List;
import io.vavr.collection.Seq;

@Named("FindBeans")
public class FindBeans implements Function1<RoundEnvironment, Seq<Bean>> {

  @Override
  public Seq<Bean> apply(RoundEnvironment env) {
    return listEnrichedFields(env)
      .distinct()
      .map(Bean::new);
  }

  private List<TypeElement> listEnrichedFields(RoundEnvironment env){
    return List.ofAll(env.getElementsAnnotatedWith(Enrich.class))
      .map(element -> (TypeElement) element.getEnclosingElement());

  }

  @Inject public FindBeans(){}
}
