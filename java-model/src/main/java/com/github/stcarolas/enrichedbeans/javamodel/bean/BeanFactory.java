package com.github.stcarolas.enrichedbeans.javamodel.bean;

import javax.lang.model.element.Element;

import io.vavr.control.Option;

public interface BeanFactory {
  Option<Bean> from(Element origin);
}
