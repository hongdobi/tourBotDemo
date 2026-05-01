package org.tourBot.ai.rag.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class RagResponse {

    private String answer;
    private List<Source> sources;
}
