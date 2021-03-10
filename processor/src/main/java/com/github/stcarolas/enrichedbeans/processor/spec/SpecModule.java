package  com.github.stcarolas.enrichedbeans.processor.spec;

import dagger.Provides;
import io.vavr.collection.Seq;
import static io.vavr.API.*;

import com.github.stcarolas.enrichedbeans.processor.spec.assistingfactory.CreateAssistingFactoryBeans;

@dagger.Module
public class SpecModule {
  @Provides
  public Seq<CanProcessBeans> processors(
    CreateAssistingFactoryBeans createAssistingFactory
  ){
    return Seq(createAssistingFactory);
  }

}
