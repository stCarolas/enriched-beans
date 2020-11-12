package com.github.stcarolas.enrichedbeans.processor.functions;

import java.util.function.Function;
import javax.annotation.processing.ProcessingEnvironment;
import com.github.stcarolas.enrichedbeans.processor.AssistingFactorySpec;
import com.github.stcarolas.enrichedbeans.processor.FactoryVisibility;
import com.github.stcarolas.enrichedbeans.processor.Field;
import com.github.stcarolas.enrichedbeans.processor.ImmutableAssistingFactorySpec;
import com.github.stcarolas.enrichedbeans.processor.TargetBean;
import org.immutables.value.Value;
import io.vavr.Function2;

public class CreateSpec
    implements Function2<TargetBean, ProcessingEnvironment, AssistingFactorySpec> {

    @Override
    public AssistingFactorySpec apply(TargetBean bean, ProcessingEnvironment env) {
        return ImmutableAssistingFactorySpec.builder()
            .packageName(bean.packageName())
            .factoryClassName(bean.name() + "Factory")
            .factoryMethodName(detectFactoryMethodName(env))
            .targetType(bean.type())
            .visibility(detectVisibility(env))
            .defineAsVavrFunctionInterface(detectUseVavr(env))
            .instanceFields(bean.allFields().reject(Field::isEnriched))
            .injectingFields(bean.allFields().filter(Field::isEnriched))
            .build();
    }

    private boolean detectUseVavr(ProcessingEnvironment env) {
        return "true".equals(env.getOptions().get("useVavr"));
    }

    private String detectFactoryMethodName(ProcessingEnvironment env) {
        String methodName = env.getOptions().get("factoryMethodName");
        return methodName == null ? "from" : methodName;
    }

    private FactoryVisibility detectVisibility(ProcessingEnvironment env) {
        return "package".equals(env.getOptions().get("factoryVisibility"))
            ? FactoryVisibility.PACKAGE
            : FactoryVisibility.PUBLIC;
    }
}
