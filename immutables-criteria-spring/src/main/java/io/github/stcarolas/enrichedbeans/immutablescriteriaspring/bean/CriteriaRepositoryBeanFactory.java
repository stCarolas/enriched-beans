package io.github.stcarolas.enrichedbeans.immutablescriteriaspring.bean;

import static io.vavr.API.*;

import io.github.stcarolas.enrichedbeans.immutablescriteriaspring.annotation.CriteriaRepositoryAnnotation;
import io.github.stcarolas.enrichedbeans.javamodel.Environment;
import io.github.stcarolas.enrichedbeans.javamodel.annotation.AbstractAnnotationFactory;
import io.github.stcarolas.enrichedbeans.javamodel.bean.Bean;
import io.github.stcarolas.enrichedbeans.javamodel.bean.BeanFactory;
import io.github.stcarolas.enrichedbeans.javamodel.method.AbstractMethodFactory;
import io.github.stcarolas.enrichedbeans.javamodel.variable.AbstractVariableFactory;
import io.vavr.control.Option;
import javax.inject.Inject;
import javax.lang.model.element.Element;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CriteriaRepositoryBeanFactory extends BeanFactory {

  private Logger log = LogManager.getLogger();

  @Inject
  public CriteriaRepositoryBeanFactory(
    AbstractVariableFactory variableFactory,
    AbstractAnnotationFactory annotationFactory,
    AbstractMethodFactory methodFactory,
    Environment env
  ) {
    super(variableFactory, annotationFactory, methodFactory, env);
  }

  @Override
  public Option<CriteriaRepositoryBean> from(Element origin) {
    Bean bean = defaultImplementation(origin);
    log
      .always()
      .log("Scanning bean: {}.{}", bean.packageName(), bean.className());
    if (bean.missingAnnotation(CriteriaRepositoryAnnotation.ANNOTATION_CANONICAL_NAME)) {
      log
        .always()
        .log(
          "Bean {}.{} missed Repository annotation, it has: {}",
          bean.packageName(),
          bean.className(),
          bean.annotations()
        );
      return None();
    }
    log
      .always()
      .log(
        "Bean {}.{} has Repository annotation",
        bean.packageName(),
        bean.className()
      );
    return Some(
      ImmutableCriteriaRepositoryBeanImpl.builder().from(bean).build()
    );
  }
}
