package com.github.stcarolas.enrichedbeans.processor.functions;

import javax.annotation.processing.RoundEnvironment;
import javax.inject.Inject;
import javax.inject.Named;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import com.github.stcarolas.enrichedbeans.annotations.Assisted;
import com.github.stcarolas.enrichedbeans.annotations.Enrich;
import com.github.stcarolas.enrichedbeans.processor.java.Bean;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.vavr.Function1;
import io.vavr.collection.List;
import io.vavr.collection.Seq;

@Named("FindBeans")
@SuppressWarnings("serial")
public class FindBeans
  implements Function1<RoundEnvironment, Seq<Bean>> {

  @Override
  public Seq<Bean> apply(RoundEnvironment env) {
    log.info("scaning for beans to enrich");
    return listEnrichedFields(env)
      .distinct()
      .map(Bean::new);
  }

  @SuppressWarnings("unchecked")
  private List<TypeElement> listEnrichedFields(RoundEnvironment env){
    List<Element> targetBeans = List.ofAll((java.util.Set<Element>)env.getElementsAnnotatedWith(Enrich.class))
      .map(element -> element.getEnclosingElement())
      .appendAll(
        List.ofAll(env.getElementsAnnotatedWith(Assisted.class))
      );
    targetBeans.forEach(bean -> log.info("enriched bean: {}", bean.getSimpleName()));
    return targetBeans.map(element -> (TypeElement)element);
  }

  @Inject public FindBeans(){}

  static final Logger log = LogManager.getLogger();
}
