package org.tourBot.ai.orchestrator.planner;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Plan {

    private boolean useWeather;
    private boolean useExchangeRate;
    private boolean useRag;
    private boolean needRecommend;
}
