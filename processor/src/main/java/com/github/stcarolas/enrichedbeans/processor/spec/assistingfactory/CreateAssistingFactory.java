package  com.github.stcarolas.enrichedbeans.processor.spec.assistingfactory;

import java.util.function.Function;
import javax.annotation.processing.ProcessingEnvironment;
import javax.inject.Inject;
import javax.lang.model.element.Modifier;

import com.github.stcarolas.enrichedbeans.annotations.Assisted;
import com.github.stcarolas.enrichedbeans.processor.ImmutableAssistingFactory;
import com.github.stcarolas.enrichedbeans.processor.java.Bean;
import com.github.stcarolas.enrichedbeans.processor.spec.CanProcessBeans;
import com.github.stcarolas.enrichedbeans.processor.spec.JavaClass;
import com.github.stcarolas.enrichedbeans.processor.spec.method.Method;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.immutables.value.Value;
import io.vavr.Function2;
import io.vavr.collection.List;
import io.vavr.control.Option;
import static io.vavr.API.*;

public class CreateAssistingFactory implements CanProcessBeans {

  @Inject public CreateAssistingFactory(){}

  public JavaClass apply(ProcessingEnvironment env, Bean bean) {
    return ImmutableAssistingFactory.builder()
      .targetBean(bean)
      .visibility(detectVisibility(env))
      .build();
  }

  private String detectFactoryMethodName(ProcessingEnvironment env) {
    String methodName = env.getOptions().get("factoryMethodName");
    return methodName == null ? "from" : methodName;
  }

  private Modifier detectVisibility(ProcessingEnvironment env) {
    return "package".equals(env.getOptions().get("factoryVisibility"))
      ? Modifier.DEFAULT
      : Modifier.PUBLIC;
  }

  static final Logger log = LogManager.getLogger();
}
