package com.atlas.financial.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class AsyncDocumentWorker {

    private static final Logger log = LoggerFactory.getLogger(AsyncDocumentWorker.class);

    private final DocumentRagService ragService;

    public AsyncDocumentWorker(DocumentRagService ragService) {
        this.ragService = ragService;
    }

    @Async
    public void processDocumentInBackground(Long userId, File file, String originalFilename, java.util.function.Consumer<String> callback) {
        log.info("Starting background worker thread for document indexing: {}", originalFilename);
        try {
            int chunkCount = ragService.processAndIndexDocument(userId, file, originalFilename);
            String successMsg = String.format("""
                    ✅ **DOCUMENT INDEXED & READY FOR ULTRA-PRO RAG!**
                    
                    • **Document Name:** `%s`
                    • **Processed Section Chunks:** %d Sections
                    • **Indexing Engine:** PostgreSQL + Vector Semantic Indexing
                    
                    💡 **What You Can Ask:**
                    • *"What are the key risk factors in this document?"*
                    • *"Generate an executive summary for this filing."*
                    • *"What changed compared to last year?"*
                    • *"Ask any specific financial question with page citations!"*
                    """, originalFilename, chunkCount);

            if (callback != null) {
                callback.accept(successMsg);
            }
        } catch (Exception e) {
            log.error("Failed async document processing for user {}: {}", userId, e.getMessage(), e);
            if (callback != null) {
                callback.accept("⚠️ **Document Indexing Error:** Failed to index " + originalFilename + ". Please try uploading again.");
            }
        }
    }
}
