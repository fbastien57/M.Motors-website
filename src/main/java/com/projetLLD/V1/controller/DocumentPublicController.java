package com.projetLLD.V1.controller;

import com.projetLLD.V1.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class DocumentPublicController {

    private final DocumentService documentService;

    @GetMapping("/documents")
    public String publicDocs(Model model) {

        model.addAttribute("documents", documentService.getVisibleDocuments());

        return "documents/list";
    }
}
