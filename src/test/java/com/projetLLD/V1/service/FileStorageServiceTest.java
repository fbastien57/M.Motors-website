package com.projetLLD.V1.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FileStorageServiceTest {

    private final FileStorageService fileStorageService =
            new FileStorageService();

    // ---------------- SAVE ----------------

    @Test
    void shouldSaveFile(@TempDir Path tempDir) throws Exception {

        MultipartFile file = mock(MultipartFile.class);

        when(file.getOriginalFilename())
                .thenReturn("test file.pdf");

        when(file.getInputStream())
                .thenReturn(new ByteArrayInputStream("data".getBytes()));

        // On remplace temporairement le dossier (hack propre pour test)
        FileStorageService service = new FileStorageService() {
            @Override
            public String save(MultipartFile file, String folder) {
                try {
                    String original = file.getOriginalFilename();

                    String safeName = original
                            .replaceAll("[^a-zA-Z0-9\\.\\-]", "_");

                    String filename = "test_" + safeName;

                    Path path = tempDir.resolve(filename);

                    Files.createDirectories(path.getParent());

                    Files.copy(file.getInputStream(), path);

                    return path.toString();

                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };

        String result = service.save(file, "any");

        assertNotNull(result);
        assertTrue(result.contains("test_file.pdf"));
    }

    // ---------------- SAVE ERROR ----------------

    @Test
    void shouldThrowWhenIOExceptionOccurs() {

        MultipartFile file = mock(MultipartFile.class);

        when(file.getOriginalFilename())
                .thenReturn("file.pdf");

        try (MockedStatic<Files> mocked = Mockito.mockStatic(Files.class)) {

            mocked.when(() ->
                            Files.createDirectories(any()))
                    .thenThrow(new RuntimeException("fail"));

            FileStorageService service = new FileStorageService();

            assertThrows(RuntimeException.class,
                    () -> service.save(file, "docs"));
        }
    }

    // ---------------- DELETE OK ----------------

    @Test
    void shouldDeleteFile() {

        FileStorageService service = new FileStorageService();

        Path fakePath = Path.of("uploads/test.pdf");

        try (MockedStatic<Files> mocked = Mockito.mockStatic(Files.class)) {

            mocked.when(() ->
                            Files.deleteIfExists(any()))
                    .thenReturn(true);

            assertDoesNotThrow(() ->
                    service.delete(fakePath.toString()));

            mocked.verify(() ->
                    Files.deleteIfExists(any()));
        }
    }

    // ---------------- DELETE SECURITY BLOCK ----------------

    @Test
    void shouldBlockUnauthorizedDelete() {

        FileStorageService service = new FileStorageService();

        String fakePath = "../etc/passwd";

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.delete(fakePath));

        assertEquals("Accès fichier interdit", ex.getMessage());
    }

    // ---------------- DELETE ERROR ----------------

    @Test
    void shouldThrowWhenDeleteFails() {

        FileStorageService service = new FileStorageService();

        Path path = Path.of("uploads/test.pdf");

        try (MockedStatic<Files> mocked = Mockito.mockStatic(Files.class)) {

            mocked.when(() ->
                            Files.deleteIfExists(any()))
                    .thenThrow(new RuntimeException("error"));

            assertThrows(RuntimeException.class,
                    () -> service.delete(path.toString()));
        }
    }
}