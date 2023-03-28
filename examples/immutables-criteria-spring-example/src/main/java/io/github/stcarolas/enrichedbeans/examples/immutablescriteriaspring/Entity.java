package io.github.stcarolas.enrichedbeans.examples.immutablescriteriaspring;

import java.util.UUID;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import org.immutables.criteria.Criteria;
import org.immutables.value.Value;

@Value.Immutable
@Criteria
@Criteria.Repository
@JsonSerialize(as = ImmutableEntity.class)
@JsonDeserialize(as = ImmutableEntity.class)
public abstract class Entity {

    @Criteria.Id
    @Value.Default
    public String id() {
        return UUID.randomUUID().toString();
    }

    public abstract String value();

}
