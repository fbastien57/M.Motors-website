package com.projetLLD.V1.service;

import com.projetLLD.V1.dto.DocumentDTO;
import com.projetLLD.V1.entity.Document;
import com.projetLLD.V1.enums.DocumentCategory;
import com.projetLLD.V1.repository.DocumentRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository repository;
    private final FileStorageService fileStorageService;

    public List<Document> findAll() {
        return repository.findAll();
    }

    public List<Document> getVisibleDocuments() {
        return repository.findAll()
                .stream()
                .filter(Document::isVisible)
                .toList();
    }

    // UPLOAD
    public void create(DocumentDTO dto) {

        if (dto.getFile() == null || dto.getFile().isEmpty()) {
            throw new IllegalArgumentException("Fichier obligatoire");
        }

        String path = fileStorageService.save(dto.getFile(), "public-documents");

        Document doc = Document.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .category(dto.getCategory())
                .filePath(path)
                .visible(true)
                .build();

        repository.save(doc);
    }

    // TOGGLE VISIBILITY
    public void toggleVisibility(Long id) {

        Document doc = repository.findById(id)
                .orElseThrow();

        doc.setVisible(!doc.isVisible());

        repository.save(doc);
    }

    public void delete(Long id) {

        Document doc = repository.findById(id)
                .orElseThrow();

        fileStorageService.delete(doc.getFilePath());

        repository.delete(doc);
    }

    public Document getById(Long id) {
        return repository.findById(id)
                .orElseThrow();
    }
}
