package  com.github.stcarolas.enrichedbeans.processor;

import org.immutables.value.Value;

import lombok.RequiredArgsConstructor;

@Value.Immutable
public interface AssistingFactoryConfig {
  String factoryMethodName();
}
