package com.EastCloud.smart_cs.service;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.parser.apache.pdfbox.ApachePdfBoxDocumentParser;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.InputStream;
import java.util.List;

@Service
public class RAGService {

    @Value("${qwen.api.key}")
    private String apiKey;

    @Value("${qwen.api.url}")
    private String baseUrl;

    private InMemoryEmbeddingStore<TextSegment> embeddingStore;
    private EmbeddingModel embeddingModel;

    @PostConstruct
    public void init() {
        embeddingStore = new InMemoryEmbeddingStore<>();
        // Qwen supports text-embedding-v2, which is compatible with OpenAI endpoint format.
        embeddingModel = OpenAiEmbeddingModel.builder()
                .apiKey(apiKey)
                .baseUrl(baseUrl)
                .modelName("text-embedding-v2")
                .build();
    }

    public void ingestDocument(MultipartFile file) throws Exception {
        try (InputStream inputStream = file.getInputStream()) {
            ApachePdfBoxDocumentParser parser = new ApachePdfBoxDocumentParser();
            Document document = parser.parse(inputStream);
            
            EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
                    .documentSplitter(dev.langchain4j.data.document.splitter.DocumentSplitters.recursive(500, 50))
                    .embeddingModel(embeddingModel)
                    .embeddingStore(embeddingStore)
                    .build();
                    
            ingestor.ingest(document);
        }
    }

    public List<dev.langchain4j.store.embedding.EmbeddingMatch<TextSegment>> search(String query, int maxResults) {
        dev.langchain4j.data.embedding.Embedding queryEmbedding = embeddingModel.embed(query).content();
        return embeddingStore.findRelevant(queryEmbedding, maxResults, 0.7);
    }
}
