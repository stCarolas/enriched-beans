package com.github.stcarolas.enrichedbeans.processor;

import com.github.stcarolas.enrichedbeans.javamodel.SourceFile;
import com.github.stcarolas.enrichedbeans.processor.domain.Environment;
import com.github.stcarolas.enrichedbeans.processor.spec.SpecModule;
import dagger.Component;
import io.vavr.collection.List;

@Component(modules = { SpecModule.class, Environment.class, SourceGeneratingModule.class })
public interface EnrichProcessorComponent {
  List<SourceFile> generated();
}
