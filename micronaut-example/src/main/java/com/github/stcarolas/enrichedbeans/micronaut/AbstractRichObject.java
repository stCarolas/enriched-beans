package  com.github.stcarolas.enrichedbeans.micronaut;

import java.util.function.Consumer;
import java.util.function.Function;

import javax.inject.Named;

import com.github.stcarolas.enrichedbeans.annotations.Enrich;
import com.github.stcarolas.enrichedbeans.annotations.FunctionAsMethod;
import com.github.stcarolas.enrichedbeans.annotations.Implement;

import io.vavr.Function0;
import io.vavr.Function1;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AbstractRichObject {

  @NonNull
  private String name;

  @NonNull @Enrich @Named("ProcessName")
  private Function1<String, String> processName;

  @NonNull @Enrich @Named("AbstractRichObjectFactory")
  private Function1<String, AbstractRichObject> createNewRichObject;

  //@FunctionAsMethod
  //private Function0<AbstractRichObject> process = () -> processName.andThen(createNewRichObject).apply(name);

}
