package org.tourBot.ai.prompt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "ai.prompt")
public class PromptProperties {

    private String orchestrator;
    private String recommendAgent;
    private String ragAgent;
    private String dbAgent;

}
