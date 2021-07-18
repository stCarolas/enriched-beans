package com.github.stcarolas.enrichedbeans.processor;

import com.github.stcarolas.enrichedbeans.processor.domain.Environment;
import com.github.stcarolas.enrichedbeans.processor.functions.FindAndEnrichBeans;
import com.github.stcarolas.enrichedbeans.processor.spec.SpecModule;
import dagger.Component;

@Component(modules = { SpecModule.class, Environment.class })
public interface EnrichProcessorComponent {
  FindAndEnrichBeans findAndEnrichBeans();
}
