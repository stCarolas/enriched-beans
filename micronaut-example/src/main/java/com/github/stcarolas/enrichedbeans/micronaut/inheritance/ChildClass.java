package com.github.stcarolas.enrichedbeans.micronaut.inheritance;

import com.github.stcarolas.enrichedbeans.annotations.Assisted;
import org.immutables.value.Value.Immutable;

@Assisted(useBuilder = true)
@Immutable
public abstract class ChildClass extends ParentClass {

  abstract public String subname();
}
