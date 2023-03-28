package io.github.stcarolas.enrichedbeans.immutablescriteriaspring.dagger;

import dagger.BindsInstance;
import dagger.Component;
import io.github.stcarolas.enrichedbeans.baseprocessor.BeanProcessorComponent;
import io.github.stcarolas.enrichedbeans.baseprocessor.ImmutableProcessingResultImpl;
import io.github.stcarolas.enrichedbeans.baseprocessor.ProcessingResult;
import io.github.stcarolas.enrichedbeans.baseprocessor.modules.BeansModule;
import io.github.stcarolas.enrichedbeans.baseprocessor.modules.ConfigFactoryModule;
import io.github.stcarolas.enrichedbeans.baseprocessor.modules.MethodFactoriesModule;
import io.github.stcarolas.enrichedbeans.baseprocessor.modules.VariableFactoriesModule;
import io.github.stcarolas.enrichedbeans.immutablescriteriaspring.bean.CriteriaRepositoryBean;
import io.vavr.collection.Seq;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.inject.Named;

@Component(
  modules = {
    VariableFactoriesModule.class,
    MethodFactoriesModule.class,
    BeansModule.class,
    ConfigFactoryModule.class,
    AnnotationFactoriesModule.class,
    BeanFactoriesModule.class,
    CriteriaRepositoryBeansModule.class,
  }
)
public abstract class ImmutablesCriteriaSpringComponent
  implements BeanProcessorComponent {

  @Named("CriteriaRepositoryBeans")
  public abstract Seq<CriteriaRepositoryBean> beans();

  @Component.Builder
  public interface Builder {
    @BindsInstance
    Builder roundEnv(RoundEnvironment roundEnv);

    @BindsInstance
    Builder processingEnv(ProcessingEnvironment processingEnv);

    ImmutablesCriteriaSpringComponent build();
  }

  @Override
  public ProcessingResult process() {
    return ImmutableProcessingResultImpl
      .builder()
      .beans(beans().map(CriteriaRepositoryBean::enrich))
      .isFinished(true)
      .build();
  }
}
