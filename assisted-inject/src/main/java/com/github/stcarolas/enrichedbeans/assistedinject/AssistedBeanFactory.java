package com.github.stcarolas.enrichedbeans.assistedinject;

import javax.inject.Inject;
import javax.inject.Named;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import com.github.stcarolas.enrichedbeans.javamodel.bean.Bean;
import com.github.stcarolas.enrichedbeans.javamodel.bean.BeanFactory;
import com.squareup.javapoet.TypeSpec;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import io.vavr.Function2;
import io.vavr.control.Option;
import io.vavr.control.Try;

@Named
public class AssistedBeanFactory implements BeanFactory {
  private static final Logger log = LogManager.getLogger();

  private final Modifier visibility;
  private final Function2<String, TypeSpec, Try<Void>> writeSourceFileFn;
  private final String factoryClassNameSuffix;

  @Inject
  public AssistedBeanFactory(
    @Named("visibility") Option<String> visibility,
    @Named("factoryClassNameSuffix") Option<String> factoryClassNameSuffix,
    @Named("WriteSourceFileFn") Function2<String, TypeSpec, Try<Void>> writeSourceFileFn
  ) {
    this.visibility =
      visibility.exists("package"::equals) ? Modifier.DEFAULT : Modifier.PUBLIC;
    this.writeSourceFileFn = writeSourceFileFn;
    this.factoryClassNameSuffix = factoryClassNameSuffix.getOrElse("Factory");
  }

  @Override
  public Option<Bean> from(Element origin) {
    return null;
  }
}
