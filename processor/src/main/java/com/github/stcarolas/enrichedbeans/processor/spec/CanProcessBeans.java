package  com.github.stcarolas.enrichedbeans.processor.spec;

import javax.annotation.processing.ProcessingEnvironment;

import com.github.stcarolas.enrichedbeans.processor.java.Bean;

import io.vavr.Function2;

public interface CanProcessBeans 
  extends Function2<ProcessingEnvironment, Bean, JavaClass> {

}
