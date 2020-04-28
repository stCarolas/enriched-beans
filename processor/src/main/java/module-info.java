module com.github.stcarolas.enriched {
	requires static vavr;
  requires auto.service.annotations;
  requires java.compiler;
  requires javax.inject;
  requires com.squareup.javapoet;
  requires com.github.stcarolas.enrichedbeans.annotations;

  exports com.github.stcarolas.enrichedbeans.processor;
}

