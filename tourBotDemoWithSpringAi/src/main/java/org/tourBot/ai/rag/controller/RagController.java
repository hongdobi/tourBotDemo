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
@RequestMapping("/api/rag")
public class RagController {

    private final RagService ragService;

    @PostMapping("/upload")
    public String upload(@RequestParam MultipartFile file) {
        return ragService.upload(file);
    }

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
