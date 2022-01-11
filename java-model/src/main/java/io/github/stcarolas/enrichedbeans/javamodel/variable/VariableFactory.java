package io.github.stcarolas.enrichedbeans.javamodel.variable;

import javax.lang.model.element.Element;
import io.vavr.control.Option;

public interface VariableFactory {
  Option<Variable> from(Element element);
}
