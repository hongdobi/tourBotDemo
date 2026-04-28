package org.tourBot.ai.rag.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.tourBot.ai.rag.service.RagService;
import org.tourBot.dto.IngestRequest;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/rag")
public class RagController {

    private final RagService ragService;

    @PostMapping("/ingest")
    public String ingest(@RequestBody IngestRequest req) {

            ragService.ingest(
                    req.getFileId(),
                    req.getFilePath(),
                    req.getFileName()
            );
            return "PDF ingested";
    }

    @DeleteMapping("/{fileId}")
    public String delete(@PathVariable String fileId) {

        ragService.delete(fileId);

        return "deleted";
    }
}
