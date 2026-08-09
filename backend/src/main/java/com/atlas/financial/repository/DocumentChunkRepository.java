package com.atlas.financial.repository;

import com.atlas.financial.model.DocumentChunk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentChunkRepository extends JpaRepository<DocumentChunk, Long> {

    List<DocumentChunk> findByUserId(Long userId);

    List<DocumentChunk> findByUserIdAndDocumentName(Long userId, String documentName);

    List<DocumentChunk> findByUserIdAndChunkTextContainingIgnoreCase(Long userId, String keyword);

    List<DocumentChunk> findByUserIdAndSectionNameContainingIgnoreCase(Long userId, String sectionKeyword);
}
