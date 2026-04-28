package org.tourBot.history.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.tourBot.history.service.FileService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/files")
public class FileController {

    private final FileService fileService;

    @PostMapping("/upload")
    public String upload(@RequestParam MultipartFile file) {
        return fileService.upload(file);
    }

    @DeleteMapping("/{fileId}")
    public String delete(@PathVariable String fileId) {
        fileService.delete(fileId);
        return "deleted";
    }
}
