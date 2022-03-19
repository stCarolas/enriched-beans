package  io.github.stcarolas.enrichedbeans.assistedinject;

import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;

import com.google.auto.service.AutoService;

import io.github.stcarolas.enrichedbeans.assistedinject.dagger.DaggerAssistedInjectComponent;
import io.github.stcarolas.enrichedbeans.baseprocessor.BaseEnrichedBeansProcessor;
import io.github.stcarolas.enrichedbeans.baseprocessor.BeanProcessorComponent;
import io.vavr.collection.Seq;
import static io.vavr.API.*;

@AutoService(javax.annotation.processing.Processor.class)
@SupportedAnnotationTypes(
  {
    "io.github.stcarolas.enrichedbeans.annotations.Enrich",
    "io.github.stcarolas.enrichedbeans.annotations.Assisted"
  }
)
public class AssistedProcessor extends BaseEnrichedBeansProcessor {

  @Override
  public Seq<BeanProcessorComponent> components(RoundEnvironment roundEnv) {
    return Seq(
      DaggerAssistedInjectComponent.builder()
        .roundEnv(roundEnv)
        .processingEnv(processingEnv)
        .build()
    );
  }

}
