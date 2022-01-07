package com.github.stcarolas.enrichedbeans.assistedinject;

import static io.vavr.API.Some;
import javax.inject.Inject;
import javax.inject.Named;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import com.github.stcarolas.enrichedbeans.javamodel.Environment;
import com.github.stcarolas.enrichedbeans.javamodel.annotation.AbstractAnnotationFactory;
import com.github.stcarolas.enrichedbeans.javamodel.bean.Bean;
import com.github.stcarolas.enrichedbeans.javamodel.bean.BeanFactory;
import com.github.stcarolas.enrichedbeans.javamodel.method.AbstractMethodFactory;
import com.github.stcarolas.enrichedbeans.javamodel.variable.AbstractVariableFactory;
import com.squareup.javapoet.TypeSpec;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import io.vavr.Function2;
import io.vavr.control.Option;
import io.vavr.control.Try;

@Named
public class AssistedBeanFactory extends BeanFactory {
  private static final Logger log = LogManager.getLogger();
  private static final String DEFAULT_FACTORY_METHOD_NAME = "from";

  private final Modifier visibility;
  private final Function2<String, TypeSpec, Try<Void>> writeSourceFileFn;
  private final String factoryClassNameSuffix;
  private final String factoryMethodName;

  @Inject
  public AssistedBeanFactory(
    AbstractVariableFactory variableFactory,
    AbstractAnnotationFactory annotationFactory,
    AbstractMethodFactory methodFactory,
    Environment env
  ) {
    super(variableFactory, annotationFactory, methodFactory);
    visibility = env.getOption("factoryVisibility").exists("package"::equals) 
      ? Modifier.DEFAULT
      : Modifier.PUBLIC;
    writeSourceFileFn = env.writeSourceFileFn();
    factoryClassNameSuffix = env.getOption("factoryClassNameSuffix").getOrElse("Factory");
    factoryMethodName = env.getOption("factoryMethodName").getOrElse(DEFAULT_FACTORY_METHOD_NAME);
    log.info(
      "AssistedBeanFactory initialized (factoryClassNameSuffix={},factoryMethodName={},factoryVisibility={})",
      factoryClassNameSuffix,
      factoryMethodName,
      visibility
    );
  }

  @Override
  public Option<Bean> from(Element origin) {
    return Some(
      ImmutableAssistedBeanImpl.builder()
        .from(super.defaultImplementation(origin))
        .writeSourceFileFn(writeSourceFileFn)
        .factoryVisibility(visibility)
        .factoryClassNameSuffix(factoryClassNameSuffix)
        .factoryMethodName(factoryMethodName)
        .build()
    );
  }
}
