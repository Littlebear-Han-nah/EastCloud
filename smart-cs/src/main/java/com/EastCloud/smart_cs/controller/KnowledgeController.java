package com.EastCloud.smart_cs.controller;

import com.EastCloud.smart_cs.service.RAGService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/knowledge")
public class KnowledgeController {

    @Autowired
    private RAGService ragService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadKnowledge(@RequestParam("file") MultipartFile file) {
        try {
            ragService.ingestDocument(file);
            return ResponseEntity.ok("File ingested successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error processing file: " + e.getMessage());
        }
    }
}
