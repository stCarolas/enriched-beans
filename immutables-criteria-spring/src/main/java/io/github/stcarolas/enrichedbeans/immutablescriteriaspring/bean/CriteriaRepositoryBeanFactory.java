package io.github.stcarolas.enrichedbeans.immutablescriteriaspring.bean;

import static io.vavr.API.*;

import com.squareup.javapoet.TypeName;
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

public class CriteriaRepositoryBeanFactory extends BeanFactory {

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
    if (bean.isAbstract()) {
      return None();
    }
    if (bean.missingAnnotation(CriteriaRepositoryAnnotation.class)) {
      return None();
    }
    return Some(
      ImmutableCriteriaRepositoryBeanImpl
        .builder()
        .type(TypeName.get(origin.asType()))
        .from(bean)
        .build()
    );
  }
}
