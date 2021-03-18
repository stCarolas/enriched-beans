package  com.github.stcarolas.enrichedbeans.processor.java;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.immutables.value.Value.Immutable;

import io.vavr.collection.Map;

@Immutable
public interface BeanBuilder {

  String packageName();
  String className();
  String newBuilderMethod();

  default String build(){
    //String settingsParametersLine = fields().foldLeft(
        //new StringBuilder(),
        //(builder, parameters) -> builder.append(String.format(".%s(%s)",parameters._1,parameters._2))
      //).toString();
    return String.format(
        "%s.%s.%s()",
        packageName(),className(),newBuilderMethod()
      );
  }
  
  static final Logger log = LogManager.getLogger();

}
