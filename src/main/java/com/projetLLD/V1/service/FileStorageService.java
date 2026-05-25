package com.projetLLD.V1.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    private final String baseDir = "uploads/";

    public String save(MultipartFile file, String folder) {

        try {
            String original = file.getOriginalFilename();

            String safeName = original
                    .replaceAll("[^a-zA-Z0-9\\.\\-]", "_");

            String filename = UUID.randomUUID() + "_" + safeName;

            Path path = Paths.get(baseDir + folder, filename);

            Files.createDirectories(path.getParent());

            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

            return path.toString();

        } catch (IOException e) {
            throw new RuntimeException("File upload error", e);
        }
    }

    public void delete(String filePath) {

        Path basePath = Paths.get(baseDir).toAbsolutePath().normalize();
        Path path = Paths.get(filePath).toAbsolutePath().normalize();

        if (!path.startsWith(basePath)) {
            throw new RuntimeException("Accès fichier interdit");
        }

        try {
            Files.deleteIfExists(path);
        } catch (Exception e) {
            throw new RuntimeException("Erreur suppression fichier : " + filePath, e);
        }
    }

   /* public void delete(String filePath) {

        try {
            Path path = Paths.get(filePath);

            // sécurité : éviter suppression hors uploads
            if (!path.startsWith(Paths.get(baseDir))) {
                throw new RuntimeException("Accès fichier interdit");
            }

            Files.deleteIfExists(path);

        } catch (Exception e) {
            throw new RuntimeException("Erreur suppression fichier : " + filePath, e);
        }
    }*/
}
