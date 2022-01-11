package  io.github.stcarolas.enrichedbeans.javamodel.method;

import javax.lang.model.element.Element;

import io.vavr.control.Option;

public interface MethodFactory {
    public Option<Method> from(Element element);
}
