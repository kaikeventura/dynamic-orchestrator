package com.kaikeventura.dynamic_orchestrator.model.orchestrator.step;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;

@Data
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "tipo",
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = PassoAPI.class, name = "API"),
        @JsonSubTypes.Type(value = PassoDynamoDB.class, name = "DYNAMODB")
})
public abstract class PassoBase {
    private String id;
    private String tipo;
    private int ordem;
}
