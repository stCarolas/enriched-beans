package  com.github.stcarolas.enrichedbeans.processor.spec;

import dagger.Provides;
import io.vavr.collection.Seq;
import static io.vavr.API.*;

import com.github.stcarolas.enrichedbeans.processor.spec.assistingfactory.CreateAssistingFactory;

@dagger.Module
public class SpecModule {
  @Provides
  public Seq<CanProcessBeans> processors(
    CreateAssistingFactory createAssistingFactory
  ){
    return Seq(createAssistingFactory);
  }

}
