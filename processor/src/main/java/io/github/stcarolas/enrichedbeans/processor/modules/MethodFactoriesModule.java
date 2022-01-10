package  com.github.stcarolas.enrichedbeans.processor.modules;

import dagger.Module;
import dagger.Provides;
import io.vavr.collection.Seq;

import javax.inject.Named;

import com.github.stcarolas.enrichedbeans.javamodel.method.MethodFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import static io.vavr.API.*;

@Module
public class MethodFactoriesModule {

  private static final Logger log = LogManager.getLogger();

  @Provides
  @Named("MethodFactories")
  public Seq<MethodFactory> MethodFactories(){
    return Seq();
  }
}
