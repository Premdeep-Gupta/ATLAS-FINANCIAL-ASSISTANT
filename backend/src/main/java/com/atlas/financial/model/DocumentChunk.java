package com.atlas.financial.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "document_chunks")
public class DocumentChunk {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "document_name", nullable = false)
    private String documentName;

    @Column(name = "document_type")
    private String documentType;

    @Column(name = "page_number")
    private Integer pageNumber;

    @Column(name = "section_name")
    private String sectionName;

    @Column(name = "chunk_text", columnDefinition = "TEXT")
    private String chunkText;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public DocumentChunk() {}

    public DocumentChunk(Long userId, String documentName, String documentType, Integer pageNumber, String sectionName, String chunkText) {
        this.userId = userId;
        this.documentName = documentName;
        this.documentType = documentType;
        this.pageNumber = pageNumber;
        this.sectionName = sectionName;
        this.chunkText = chunkText;
        this.createdAt = LocalDateTime.now();
    }

    public static DocumentChunkBuilder builder() {
        return new DocumentChunkBuilder();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getDocumentName() { return documentName; }
    public void setDocumentName(String documentName) { this.documentName = documentName; }

    public String getDocumentType() { return documentType; }
    public void setDocumentType(String documentType) { this.documentType = documentType; }

    public Integer getPageNumber() { return pageNumber; }
    public void setPageNumber(Integer pageNumber) { this.pageNumber = pageNumber; }

    public String getSectionName() { return sectionName; }
    public void setSectionName(String sectionName) { this.sectionName = sectionName; }

    public String getChunkText() { return chunkText; }
    public void setChunkText(String chunkText) { this.chunkText = chunkText; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static class DocumentChunkBuilder {
        private Long userId;
        private String documentName;
        private String documentType;
        private Integer pageNumber;
        private String sectionName;
        private String chunkText;

        public DocumentChunkBuilder userId(Long userId) { this.userId = userId; return this; }
        public DocumentChunkBuilder documentName(String documentName) { this.documentName = documentName; return this; }
        public DocumentChunkBuilder documentType(String documentType) { this.documentType = documentType; return this; }
        public DocumentChunkBuilder pageNumber(Integer pageNumber) { this.pageNumber = pageNumber; return this; }
        public DocumentChunkBuilder sectionName(String sectionName) { this.sectionName = sectionName; return this; }
        public DocumentChunkBuilder chunkText(String chunkText) { this.chunkText = chunkText; return this; }

        public DocumentChunk build() {
            return new DocumentChunk(userId, documentName, documentType, pageNumber, sectionName, chunkText);
        }
    }
}
