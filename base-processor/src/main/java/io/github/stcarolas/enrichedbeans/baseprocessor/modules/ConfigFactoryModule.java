package  io.github.stcarolas.enrichedbeans.baseprocessor.modules;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import dagger.Module;
import dagger.Provides;

@Module
public class ConfigFactoryModule {

  private static final Logger log = LogManager.getLogger();

  @Provides
  public Config configFactory(){
    return ConfigFactory.load();
  }
}
