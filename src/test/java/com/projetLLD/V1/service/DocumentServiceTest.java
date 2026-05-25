package com.projetLLD.V1.service;

import com.projetLLD.V1.dto.DocumentDTO;
import com.projetLLD.V1.entity.Document;
import com.projetLLD.V1.enums.DocumentCategory;
import com.projetLLD.V1.repository.DocumentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentServiceTest {

    @Mock
    private DocumentRepository repository;

    @Mock
    private FileStorageService fileStorageService;

    @InjectMocks
    private DocumentService documentService;

    // ---------------- FIND ALL ----------------

    @Test
    void shouldReturnAllDocuments() {

        when(repository.findAll())
                .thenReturn(List.of(new Document(), new Document()));

        List<Document> result = documentService.findAll();

        assertEquals(2, result.size());

        verify(repository).findAll();
    }

    // ---------------- GET VISIBLE ----------------

    @Test
    void shouldReturnOnlyVisibleDocuments() {

        Document visible = new Document();
        visible.setVisible(true);

        Document hidden = new Document();
        hidden.setVisible(false);

        when(repository.findAll())
                .thenReturn(List.of(visible, hidden));

        List<Document> result = documentService.getVisibleDocuments();

        assertEquals(1, result.size());
        assertTrue(result.get(0).isVisible());
    }

    // ---------------- CREATE SUCCESS ----------------

    @Test
    void shouldCreateDocument() {

        MultipartFile file = mock(MultipartFile.class);

        DocumentDTO dto = new DocumentDTO();
        dto.setName("Doc 1");
        dto.setDescription("desc");
        dto.setCategory(DocumentCategory.LOA_FORM);
        dto.setFile(file);

        when(file.isEmpty()).thenReturn(false);

        when(fileStorageService.save(file, "public-documents"))
                .thenReturn("uploads/doc.pdf");

        when(repository.save(any(Document.class)))
                .thenAnswer(i -> i.getArgument(0));

        documentService.create(dto);

        verify(fileStorageService)
                .save(file, "public-documents");

        verify(repository)
                .save(any(Document.class));
    }

    // ---------------- CREATE ERROR ----------------

    @Test
    void shouldThrowWhenFileMissing() {

        DocumentDTO dto = new DocumentDTO();
        dto.setFile(null);

        assertThrows(IllegalArgumentException.class,
                () -> documentService.create(dto));

        verify(repository, never()).save(any());
    }

    @Test
    void shouldThrowWhenFileEmpty() {

        MultipartFile file = mock(MultipartFile.class);

        when(file.isEmpty()).thenReturn(true);

        DocumentDTO dto = new DocumentDTO();
        dto.setFile(file);

        assertThrows(IllegalArgumentException.class,
                () -> documentService.create(dto));
    }

    // ---------------- TOGGLE VISIBILITY ----------------

    @Test
    void shouldToggleVisibility() {

        Document doc = new Document();
        doc.setVisible(true);

        when(repository.findById(1L))
                .thenReturn(Optional.of(doc));

        documentService.toggleVisibility(1L);

        assertFalse(doc.isVisible());

        verify(repository).save(doc);
    }

    // ---------------- DELETE ----------------

    @Test
    void shouldDeleteDocument() {

        Document doc = new Document();
        doc.setFilePath("uploads/test.pdf");

        when(repository.findById(1L))
                .thenReturn(Optional.of(doc));

        documentService.delete(1L);

        verify(fileStorageService)
                .delete("uploads/test.pdf");

        verify(repository)
                .delete(doc);
    }

    // ---------------- GET BY ID ----------------

    @Test
    void shouldGetDocumentById() {

        Document doc = new Document();

        when(repository.findById(1L))
                .thenReturn(Optional.of(doc));

        Document result = documentService.getById(1L);

        assertNotNull(result);
    }

    @Test
    void shouldThrowWhenNotFound() {

        when(repository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> documentService.getById(1L));
    }
}