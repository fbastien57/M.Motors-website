package com.projetLLD.V1.controller;

import com.projetLLD.V1.config.UserPrincipal;
import com.projetLLD.V1.entity.User;
import com.projetLLD.V1.enums.DocumentType;
import com.projetLLD.V1.service.RequestDocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequiredArgsConstructor
@RequestMapping("/client/documents/")
public class RequestDocumentController {

    private final RequestDocumentService documentService;

    @PostMapping("/{requestId}/upload")
    public String uploadDocument(@PathVariable Long requestId,
                                 @RequestParam DocumentType type,
                                 @RequestParam MultipartFile file,
                                 @AuthenticationPrincipal UserPrincipal principal) {

        User user = principal.getUser();

        documentService.uploadDocument(requestId, type, file, user);

        return "redirect:/client/requests/" + requestId;
    }
}
