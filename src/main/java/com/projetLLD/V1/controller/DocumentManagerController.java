package com.projetLLD.V1.controller;

import com.projetLLD.V1.dto.DocumentDTO;
import com.projetLLD.V1.entity.Document;
import com.projetLLD.V1.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('GESTIONNAIRE','ADMIN')")
@RequestMapping("/manager/documents")
public class DocumentManagerController {

    private final DocumentService documentService;

    // LISTE ADMIN
    @GetMapping
    public String list(Model model) {

        model.addAttribute("documents", documentService.findAll());

        model.addAttribute("dto", new DocumentDTO());

        return "manager/document/list";
    }

    @GetMapping("/{id}")
    public String details(@PathVariable Long id, Model model) {

        Document doc = documentService.findAll()
                .stream()
                .filter(d -> d.getId().equals(id))
                .findFirst()
                .orElseThrow();

        model.addAttribute("document", doc);

        return "manager/document/details";
    }

    // UPLOAD
    @PostMapping("/upload")
    public String upload(@ModelAttribute DocumentDTO dto) {

        documentService.create(dto);

        return "redirect:/manager/documents";
    }

    // TOGGLE VISIBILITY
    @PostMapping("/{id}/toggle")
    public String toggle(@PathVariable Long id) {

        documentService.toggleVisibility(id);

        return "redirect:/manager/documents";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        documentService.delete(id);
        return "redirect:/manager/documents";
    }
}
