package io.github.stcarolas.enrichedbeans.processor.modules;

import javax.inject.Named;

import io.github.stcarolas.enrichedbeans.javamodel.bean.Bean;
import io.github.stcarolas.enrichedbeans.javamodel.bean.EnrichableBean;
import io.github.stcarolas.enrichedbeans.javamodel.bean.GeneratedBean;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dagger.Module;
import dagger.Provides;
import io.vavr.collection.Seq;
import io.vavr.control.Try;

@Module
public class SourceFilesModule {
  private static final Logger log = LogManager.getLogger();

  @Provides
  public Try<Seq<GeneratedBean>> sourceFiles(@Named("Beans") Seq<Bean> beans) {
    return Try.sequence(
      beans.filter(bean -> bean instanceof EnrichableBean)
        .map(it -> (EnrichableBean) it)
        .map(EnrichableBean::process)
    )
      .map(seq -> seq.flatMap(it -> it));
  }
}
