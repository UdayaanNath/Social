package org.nath.sns.config;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KafkaConfig {
    @NotEmpty
    private String bootstrapServers;
}
