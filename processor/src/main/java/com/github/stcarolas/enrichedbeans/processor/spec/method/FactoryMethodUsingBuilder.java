package  com.github.stcarolas.enrichedbeans.processor.spec.method;

import javax.lang.model.element.Modifier;

import com.github.stcarolas.enrichedbeans.processor.java.BeanBuilder;
import com.github.stcarolas.enrichedbeans.processor.java.Variable;
import com.github.stcarolas.enrichedbeans.processor.spec.method.api.MethodWithSpec;
import com.squareup.javapoet.MethodSpec;
import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Style;

import io.vavr.collection.Seq;

@Style(overshadowImplementation = true)
@Immutable public abstract class FactoryMethodUsingBuilder
  implements MethodWithSpec {

  abstract BeanBuilder beanBuilder();
  abstract Seq<Variable> objectFields();

  public MethodSpec spec() {
    String settingsParametersLine = objectFields().foldLeft(
        new StringBuilder(),
        (builder, parameter) -> builder.append(String.format(".%s(%s)",parameter.name(),parameter.accessor()))
      ).toString();
    MethodSpec.Builder method = MethodSpec.methodBuilder(name())
      .returns(returnType())
      .addModifiers(Modifier.PUBLIC)
      .addCode(String.format("return %s%s; ",beanBuilder().build(), settingsParametersLine));

    method = parameters().map(Variable::asParameterSpec)
      .foldLeft(method, MethodSpec.Builder::addParameter);

    return method.build();
	}

}
