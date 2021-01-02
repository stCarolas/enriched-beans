package  com.github.stcarolas.enrichedbeans.micronaut.assist;

import java.util.function.Function;

import javax.inject.Named;

import com.github.stcarolas.enrichedbeans.annotations.Assisted;
import com.github.stcarolas.enrichedbeans.micronaut.ProcessName;
import com.github.stcarolas.enrichedbeans.micronaut.assist.ImmutableRichObject;
import org.immutables.value.Value;

@Value.Immutable public interface RichObject {

  @InjectEnrich String name();

}
