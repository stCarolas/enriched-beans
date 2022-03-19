package io.github.stcarolas.enrichedbeans.baseprocessor.modules;

import io.github.stcarolas.enrichedbeans.javamodel.bean.Bean;
import io.github.stcarolas.enrichedbeans.javamodel.bean.EnrichableBean;
import io.github.stcarolas.enrichedbeans.javamodel.bean.GeneratedBean;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dagger.Module;
import dagger.Provides;
import io.vavr.collection.HashSet;
import io.vavr.collection.Seq;
import io.vavr.control.Try;

@Module
public class SourceFilesModule {
  private static final Logger log = LogManager.getLogger();

  @Provides
  public Try<Seq<GeneratedBean>> sourceFiles(java.util.Set<Bean> beans) {
    return Try.sequence(
      HashSet.ofAll(beans)
        .filter(bean -> bean instanceof EnrichableBean)
        .map(it -> (EnrichableBean) it)
        .map(EnrichableBean::enrich)
    )
      .map(seq -> seq.flatMap(it -> it));
  }
}
