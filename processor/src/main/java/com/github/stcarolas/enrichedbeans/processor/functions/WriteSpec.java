package com.github.stcarolas.enrichedbeans.processor.functions;

import java.util.function.Function;
import javax.annotation.processing.ProcessingEnvironment;
import com.github.stcarolas.enrichedbeans.processor.AssistingFactorySpec;
import com.squareup.javapoet.JavaFile;
import org.immutables.value.Value;
import io.vavr.Function2;
import io.vavr.control.Try;

public class WriteSpec
    implements Function2<AssistingFactorySpec, ProcessingEnvironment, Try<Void>> {

    @Override
    public Try<Void> apply(AssistingFactorySpec spec, ProcessingEnvironment env) {
        return Try.run(
            () -> JavaFile.builder(spec.packageName(), spec.create())
                .build()
                .writeTo(env.getFiler())
        );
    }
}
