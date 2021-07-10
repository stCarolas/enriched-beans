package com.github.stcarolas.enrichedbeans.processor.spec.assistingfactory;

import javax.annotation.processing.ProcessingEnvironment;
import javax.inject.Inject;
import javax.lang.model.element.Modifier;
import com.github.stcarolas.enrichedbeans.processor.beans.assistingfactory.AssistingFactoryBean;
import com.github.stcarolas.enrichedbeans.processor.beans.assistingfactory.ImmutableAssistingFactoryBean;
import com.github.stcarolas.enrichedbeans.processor.java.Bean;
import com.github.stcarolas.enrichedbeans.processor.java.Method;
import com.github.stcarolas.enrichedbeans.processor.spec.CanProcessBeans;
import com.github.stcarolas.enrichedbeans.processor.spec.JavaClass;
import com.github.stcarolas.enrichedbeans.processor.spec.method.ImmutableProxyMethod;
import com.github.stcarolas.enrichedbeans.processor.spec.method.api.HasMethodSpec;
import com.github.stcarolas.enrichedbeans.processor.spec.method.api.MethodWithSpec;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import io.vavr.control.Option;
import static io.vavr.API.*;

public class CreateAssistingFactoryBeans implements CanProcessBeans {

  @Inject
  public CreateAssistingFactoryBeans() {}

  public JavaClass apply(ProcessingEnvironment env, Bean bean) {
    AssistingFactoryBean factory = ImmutableAssistingFactoryBean.builder()
      .targetBean(bean)
      .visibility(detectVisibility(env))
      .build();
    factory = factory.withMethod(proxyFactoryMethod(env, factory.factoryMethod()));
    return factory;
  }

  private Option<HasMethodSpec> proxyFactoryMethod(
    ProcessingEnvironment env,
    Method method
  ) {
    return Option(env.getOptions().get("factoryMethodName"))
      .filter(name -> !name.isBlank())
      .map(name -> ImmutableProxyMethod.builder().method(method).name(name).build());
  }

  private Modifier detectVisibility(ProcessingEnvironment env) {
    return "package".equals(env.getOptions().get("factoryVisibility")) ? Modifier.DEFAULT
      : Modifier.PUBLIC;
  }

  static final Logger log = LogManager.getLogger();
}
