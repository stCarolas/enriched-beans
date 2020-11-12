package com.github.stcarolas.enrichedbeans.processor.functions;

import java.util.function.Function;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;
import com.github.stcarolas.enrichedbeans.annotations.Enrich;
import com.github.stcarolas.enrichedbeans.processor.TargetBean;
import org.immutables.value.Value;
import io.vavr.collection.List;
import io.vavr.collection.Seq;

public class FindBeans implements Function<RoundEnvironment, Seq<TargetBean>> {

    @Override
    public Seq<TargetBean> apply(RoundEnvironment env) {
        return List.ofAll(env.getElementsAnnotatedWith(Enrich.class))
            .map(element -> (TypeElement) element.getEnclosingElement())
            .distinct()
            .map(TargetBean::new);
    }
}
