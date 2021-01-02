package  com.github.stcarolas.enrichedbeans.processor.spec.assistingfactory;

import javax.inject.Named;

import com.github.stcarolas.enrichedbeans.processor.java.Variable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.vavr.collection.Seq;

@Named("CallBuilder")
public class CallBuilder {

  static final Logger log = LogManager.getLogger();

  public String codeLine(String builder, Seq<Variable> fields){
    String fieldSettingCodeLine = fields
      .map(field -> String.format(".%s(%s)", field.name(), field.accessor()))
      .mkString();
    return String.format("new %s()%s.build()", builder, fieldSettingCodeLine);
  }

}
