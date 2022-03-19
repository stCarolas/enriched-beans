package  io.github.stcarolas.enrichedbeans.assistedinject.dagger;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;

import dagger.BindsInstance;
import dagger.Component;
import io.github.stcarolas.enrichedbeans.baseprocessor.BeanProcessorComponent;
import io.github.stcarolas.enrichedbeans.baseprocessor.ImmutableProcessingResultImpl;
import io.github.stcarolas.enrichedbeans.baseprocessor.ProcessingResult;
import io.github.stcarolas.enrichedbeans.baseprocessor.modules.BeansModule;
import io.github.stcarolas.enrichedbeans.baseprocessor.modules.ConfigFactoryModule;
import io.github.stcarolas.enrichedbeans.baseprocessor.modules.MethodFactoriesModule;
import io.github.stcarolas.enrichedbeans.baseprocessor.modules.VariableFactoriesModule;
import io.github.stcarolas.enrichedbeans.javamodel.bean.EnrichableBean;
import io.vavr.collection.Seq;

@Component(
  modules = {
    VariableFactoriesModule.class,
    AnnotationFactoriesModule.class,
    MethodFactoriesModule.class,
    BeansModule.class,
    ConfigFactoryModule.class,
    BeanFactoriesModule.class,
    AssistInjectBeansModule.class
  }
)
public abstract class AssistedInjectComponent implements BeanProcessorComponent {

  abstract public Seq<EnrichableBean> beans();

  @Component.Builder
  public interface Builder {
    @BindsInstance
    Builder roundEnv(RoundEnvironment roundEnv);

    @BindsInstance
    Builder processingEnv(ProcessingEnvironment processingEnv);

    AssistedInjectComponent build();
  }

  @Override
  public ProcessingResult process() {
    return ImmutableProcessingResultImpl.builder()
      .beans(beans().map(EnrichableBean::enrich))
      .isFinished(true)
      .build();
  }

}
