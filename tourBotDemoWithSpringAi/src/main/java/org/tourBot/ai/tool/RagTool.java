package org.tourBot.ai.tool;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.tourBot.ai.rag.dto.Source;
import org.tourBot.ai.rag.service.RagService;

import java.util.Collections;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class RagTool {

    private final RagService ragService;

    public List<Source> search(String query) {

        log.info("RagTool search called: {}", query);

        List<Source> results = ragService.retrieve(query);

        if (results == null) {
            return Collections.emptyList();
        }

        return results;
    }
}
