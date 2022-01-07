package com.github.stcarolas.enrichedbeans.processor.modules;

import dagger.Module;
import dagger.Provides;
import io.vavr.collection.Seq;
import io.vavr.control.Try;
import javax.inject.Named;
import com.github.stcarolas.enrichedbeans.javamodel.SourceFile;
import com.github.stcarolas.enrichedbeans.javamodel.bean.Bean;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Module
public class SourceFilesModule {
  private static final Logger log = LogManager.getLogger();

  @Provides
  public Try<Seq<SourceFile>> sourceFiles(@Named("Beans") Seq<Bean> beans) {
    return Try.sequence(beans.map(Bean::process))
      .map(seq -> seq.flatMap(it -> it));
  }
}
