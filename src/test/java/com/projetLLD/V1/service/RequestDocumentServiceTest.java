package com.projetLLD.V1.service;

import com.projetLLD.V1.entity.Request;
import com.projetLLD.V1.entity.RequestDocument;
import com.projetLLD.V1.entity.User;
import com.projetLLD.V1.enums.DocumentStatus;
import com.projetLLD.V1.enums.DocumentType;
import com.projetLLD.V1.enums.RequestStatus;
import com.projetLLD.V1.repository.RequestDocumentRepository;
import com.projetLLD.V1.repository.RequestRepository;
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
class RequestDocumentServiceTest {

    @Mock
    private RequestDocumentRepository repository;

    @Mock
    private RequestRepository requestRepository;

    @Mock
    private FileStorageService fileStorageService;

    @InjectMocks
    private RequestDocumentService requestDocumentService;

    // ---------------- UPLOAD DOCUMENT SUCCESS ----------------

    @Test
    void shouldUploadDocument() {

        User user = new User();
        user.setId(1L);

        RequestDocument doc = RequestDocument.builder()
                .type(DocumentType.ID_CARD)
                .status(DocumentStatus.MISSING)
                .build();

        Request request = Request.builder()
                .user(user)
                .documents(List.of(doc))
                .status(RequestStatus.MISSING_DOCUMENT)
                .build();

        MultipartFile file = mock(MultipartFile.class);

        when(requestRepository.findById(1L))
                .thenReturn(Optional.of(request));

        when(file.isEmpty()).thenReturn(false);

        when(file.getSize()).thenReturn(1024L);

        when(file.getOriginalFilename())
                .thenReturn("identity.pdf");

        when(file.getContentType())
                .thenReturn("application/pdf");

        when(fileStorageService.save(file, "documents"))
                .thenReturn("documents/identity.pdf");

        requestDocumentService.uploadDocument(
                1L,
                DocumentType.ID_CARD,
                file,
                user
        );

        assertEquals(DocumentStatus.UPLOADED,
                doc.getStatus());

        assertEquals("documents/identity.pdf",
                doc.getFilePath());

        assertEquals("identity.pdf",
                doc.getFileName());

        verify(repository).save(doc);

        verify(requestRepository, atLeastOnce())
                .save(request);
    }

    // ---------------- USER SECURITY ----------------

    @Test
    void shouldThrowWhenUserUnauthorized() {

        User owner = new User();
        owner.setId(1L);

        User otherUser = new User();
        otherUser.setId(2L);

        Request request = Request.builder()
                .user(owner)
                .documents(List.of())
                .build();

        when(requestRepository.findById(1L))
                .thenReturn(Optional.of(request));

        MultipartFile file = mock(MultipartFile.class);

        assertThrows(RuntimeException.class,
                () -> requestDocumentService.uploadDocument(
                        1L,
                        DocumentType.ID_CARD,
                        file,
                        otherUser
                ));

        verify(repository, never()).save(any());
    }

    // ---------------- FILE VALIDATION ----------------

    @Test
    void shouldThrowWhenFileIsNull() {

        User user = new User();
        user.setId(1L); // ✅ IMPORTANT

        Request request = Request.builder()
                .user(user)
                .documents(List.of())
                .build();

        when(requestRepository.findById(1L))
                .thenReturn(Optional.of(request));

        assertThrows(IllegalArgumentException.class,
                () -> requestDocumentService.uploadDocument(
                        1L,
                        DocumentType.ID_CARD,
                        null,
                        user
                ));
    }

        @Test
    void shouldThrowWhenFileIsEmpty() {

        User user = new User();
        user.setId(1L);

        Request request = Request.builder()
                .user(user)
                .documents(List.of())
                .build();

        MultipartFile file = mock(MultipartFile.class);

        when(requestRepository.findById(1L))
                .thenReturn(Optional.of(request));

        when(file.isEmpty()).thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> requestDocumentService.uploadDocument(
                        1L,
                        DocumentType.ID_CARD,
                        file,
                        user
                ));
    }

    @Test
    void shouldThrowWhenFileTooLarge() {

        User user = new User();
        user.setId(1L);

        Request request = Request.builder()
                .user(user)
                .documents(List.of())
                .build();

        MultipartFile file = mock(MultipartFile.class);

        when(requestRepository.findById(1L))
                .thenReturn(Optional.of(request));

        when(file.isEmpty()).thenReturn(false);

        when(file.getSize())
                .thenReturn(11 * 1024 * 1024L);

        assertThrows(IllegalArgumentException.class,
                () -> requestDocumentService.uploadDocument(
                        1L,
                        DocumentType.ID_CARD,
                        file,
                        user
                ));
    }

    @Test
    void shouldThrowWhenFilenameInvalid() {

        User user = new User();
        user.setId(1L);

        Request request = Request.builder()
                .user(user)
                .documents(List.of())
                .build();

        MultipartFile file = mock(MultipartFile.class);

        when(requestRepository.findById(1L))
                .thenReturn(Optional.of(request));

        when(file.isEmpty()).thenReturn(false);

        when(file.getSize()).thenReturn(1000L);

        when(file.getOriginalFilename())
                .thenReturn("");

        assertThrows(IllegalArgumentException.class,
                () -> requestDocumentService.uploadDocument(
                        1L,
                        DocumentType.ID_CARD,
                        file,
                        user
                ));
    }

    @Test
    void shouldThrowWhenExtensionInvalid() {

        User user = new User();
        user.setId(1L);

        Request request = Request.builder()
                .user(user)
                .documents(List.of())
                .build();

        MultipartFile file = mock(MultipartFile.class);

        when(requestRepository.findById(1L))
                .thenReturn(Optional.of(request));

        when(file.isEmpty()).thenReturn(false);

        when(file.getSize()).thenReturn(1000L);

        when(file.getOriginalFilename())
                .thenReturn("virus.exe");

        assertThrows(IllegalArgumentException.class,
                () -> requestDocumentService.uploadDocument(
                        1L,
                        DocumentType.ID_CARD,
                        file,
                        user
                ));
    }

    @Test
    void shouldThrowWhenMimeTypeInvalid() {

        User user = new User();
        user.setId(1L);

        Request request = Request.builder()
                .user(user)
                .documents(List.of())
                .build();

        MultipartFile file = mock(MultipartFile.class);

        when(requestRepository.findById(1L))
                .thenReturn(Optional.of(request));

        when(file.isEmpty()).thenReturn(false);

        when(file.getSize()).thenReturn(1000L);

        when(file.getOriginalFilename())
                .thenReturn("file.pdf");

        when(file.getContentType())
                .thenReturn("application/x-msdownload");

        assertThrows(IllegalArgumentException.class,
                () -> requestDocumentService.uploadDocument(
                        1L,
                        DocumentType.ID_CARD,
                        file,
                        user
                ));
    }

    // ---------------- VALIDATE DOCUMENT ----------------

    @Test
    void shouldValidateDocument() {

        RequestDocument doc = RequestDocument.builder()
                .status(DocumentStatus.UPLOADED)
                .build();

        when(repository.findById(1L))
                .thenReturn(Optional.of(doc));

        requestDocumentService.validateDocument(1L);

        assertEquals(DocumentStatus.VALIDATED,
                doc.getStatus());

        verify(repository).save(doc);
    }

    // ---------------- REJECT DOCUMENT ----------------

    @Test
    void shouldRejectDocument() {

        RequestDocument doc = RequestDocument.builder()
                .status(DocumentStatus.UPLOADED)
                .build();

        when(repository.findById(1L))
                .thenReturn(Optional.of(doc));

        requestDocumentService.rejectDocument(
                1L,
                "Document illisible"
        );

        assertEquals(DocumentStatus.REJECTED,
                doc.getStatus());

        assertEquals("Document illisible",
                doc.getRejectionReason());

        verify(repository).save(doc);
    }

    // ---------------- REQUEST STATUS ----------------

    @Test
    void shouldSetRequestPendingWhenAllDocumentsUploaded() {

        User user = new User();
        user.setId(1L);

        RequestDocument doc = RequestDocument.builder()
                .type(DocumentType.ID_CARD)
                .status(DocumentStatus.VALIDATED)
                .build();

        Request request = Request.builder()
                .user(user)
                .documents(List.of(doc))
                .status(RequestStatus.MISSING_DOCUMENT)
                .build();

        MultipartFile file = mock(MultipartFile.class);

        when(requestRepository.findById(1L))
                .thenReturn(Optional.of(request));

        when(file.isEmpty()).thenReturn(false);

        when(file.getSize()).thenReturn(1000L);

        when(file.getOriginalFilename())
                .thenReturn("file.pdf");

        when(file.getContentType())
                .thenReturn("application/pdf");

        when(fileStorageService.save(file, "documents"))
                .thenReturn("documents/file.pdf");

        requestDocumentService.uploadDocument(
                1L,
                DocumentType.ID_CARD,
                file,
                user
        );

        assertEquals(RequestStatus.PENDING,
                request.getStatus());
    }
}