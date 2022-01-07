package  com.github.stcarolas.enrichedbeans.processor.modules;

import static io.vavr.API.Seq;

import javax.inject.Named;

import com.github.stcarolas.enrichedbeans.javamodel.variable.VariableFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dagger.Module;
import dagger.Provides;
import io.vavr.collection.Seq;

@Module
public class VariableFactoriesModule {

  private static final Logger log = LogManager.getLogger();

  @Provides
  @Named("VariableFactories")
  public Seq<VariableFactory> variableFactories(){
    return Seq();
  }
}
