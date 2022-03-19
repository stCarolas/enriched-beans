package  io.github.stcarolas.enrichedbeans.baseprocessor.modules;

import static io.vavr.API.Seq;

import javax.inject.Named;

import io.github.stcarolas.enrichedbeans.javamodel.variable.VariableFactory;

import dagger.Module;
import dagger.Provides;
import io.vavr.collection.Seq;

@Module
public class VariableFactoriesModule {

  @Provides
  @Named("VariableFactories")
  public Seq<VariableFactory> variableFactories(){
    return Seq();
  }
}
