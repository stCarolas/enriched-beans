package com.github.stcarolas.enrichedbeans.javamodel.annotation;

import javax.lang.model.element.AnnotationMirror;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import io.vavr.control.Option;

public abstract class AnnotationFactory {
  private static final Logger log = LogManager.getLogger();

  abstract protected Option<Annotation> from(AnnotationMirror mirror);
}
