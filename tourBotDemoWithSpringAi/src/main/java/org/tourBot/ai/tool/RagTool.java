package org.tourBot.ai.tool;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;
import org.tourBot.ai.rag.dto.RagRequest;
import org.tourBot.ai.rag.dto.RagResponse;
import org.tourBot.ai.rag.dto.Source;
import org.tourBot.ai.rag.service.RagService;

import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class RagTool {

    private final RagService ragService;

    @Tool(name = "searchDocuments", description = "Search internal documents and answer questions based on context")
    public String searchDocuments(String question) {

        log.info("RagTool called: {}", question);

        RagResponse response = ragService.ask(new RagRequest(question, null));

        String sources = Optional.ofNullable(response.getSources())
                .orElse(Collections.emptyList())
                .stream()
                .map(Source::getFileName)
                .collect(Collectors.joining(", "));

        return """
        [RAG_RESULT]
        Answer:
        %s
    
        Sources:
        %s
        """.formatted(response.getAnswer(), sources);
    }
}
