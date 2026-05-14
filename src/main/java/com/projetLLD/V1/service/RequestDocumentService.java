package com.projetLLD.V1.service;

import com.projetLLD.V1.entity.Request;
import com.projetLLD.V1.entity.RequestDocument;
import com.projetLLD.V1.entity.User;
import com.projetLLD.V1.enums.DocumentStatus;
import com.projetLLD.V1.enums.DocumentType;
import com.projetLLD.V1.enums.RequestStatus;
import com.projetLLD.V1.repository.RequestDocumentRepository;
import com.projetLLD.V1.repository.RequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class RequestDocumentService {

    private final RequestDocumentRepository repository;
    private final RequestRepository requestRepository;
    private final FileStorageService fileStorageService;

    public void uploadDocument(Long requestId,
                               DocumentType type,
                               MultipartFile file,
                               User user) {

        Request request = requestRepository.findById(requestId)
                .orElseThrow();

        // SECURITE USER
        if (!request.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        // VERIFICATION FICHIER
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Veuillez sélectionner un fichier");
        }

        // TAILLE MAX = 10MB
        long maxSize = 10 * 1024 * 1024;

        if (file.getSize() > maxSize) {
            throw new IllegalArgumentException("Le fichier dépasse 10MB");
        }

        // NOM FICHIER
        String originalFilename = file.getOriginalFilename();

        if (originalFilename == null || originalFilename.isBlank()) {
            throw new IllegalArgumentException("Nom de fichier invalide");
        }

        String filename = originalFilename.toLowerCase();

        // EXTENSIONS AUTORISEES
        boolean validExtension =
                filename.endsWith(".pdf")
                        || filename.endsWith(".jpg")
                        || filename.endsWith(".jpeg")
                        || filename.endsWith(".png");

        if (!validExtension) {
            throw new IllegalArgumentException(
                    "Formats autorisés : PDF, JPG, JPEG, PNG"
            );
        }

        // MIME TYPES AUTORISES
        String contentType = file.getContentType();

        boolean validMime =
                contentType != null &&
                        (
                                contentType.equals("application/pdf")
                                        || contentType.equals("image/jpeg")
                                        || contentType.equals("image/png")
                        );

        if (!validMime) {
            throw new IllegalArgumentException(
                    "Type MIME invalide"
            );
        }

        // DOCUMENT
        RequestDocument doc = request.getDocuments().stream()
                .filter(d -> d.getType() == type)
                .findFirst()
                .orElseThrow();

        // SAVE
        String path = fileStorageService.save(file, "documents");

        doc.setFilePath(path);
        doc.setFileName(originalFilename);
        doc.setStatus(DocumentStatus.UPLOADED);
        doc.setRejectionReason(null);

        repository.save(doc);

        updateRequestStatus(request);
    }

    public void validateDocument(Long documentId) {
        RequestDocument doc = repository.findById(documentId).orElseThrow();

        doc.setStatus(DocumentStatus.VALIDATED);
        repository.save(doc);
    }

    public void rejectDocument(Long documentId, String reason) {
        RequestDocument doc = repository.findById(documentId).orElseThrow();

        doc.setStatus(DocumentStatus.REJECTED);
        doc.setRejectionReason(reason);
        repository.save(doc);
    }

    private void updateRequestStatus(Request request) {

        boolean hasDocuments = !request.getDocuments().isEmpty();

        boolean allUploaded = hasDocuments &&
                request.getDocuments().stream()
                        .allMatch(d ->
                                d.getStatus() == DocumentStatus.UPLOADED
                                        || d.getStatus() == DocumentStatus.VALIDATED
                        );

        if (allUploaded) {
            request.setStatus(RequestStatus.PENDING);
        } else {
            request.setStatus(RequestStatus.MISSING_DOCUMENT);
        }

        requestRepository.save(request);
    }
}
