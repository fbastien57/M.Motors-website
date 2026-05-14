package com.projetLLD.V1.controller;

import com.projetLLD.V1.entity.Request;
import com.projetLLD.V1.repository.RequestRepository;
import com.projetLLD.V1.service.RequestDocumentService;
import com.projetLLD.V1.service.RequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/manager/requests")
@PreAuthorize("hasAnyRole('GESTIONNAIRE','ADMIN')")
public class RequestManagerController {

    private final RequestRepository requestRepository;
    private final RequestDocumentService documentService;
    private final RequestService requestService;

    @GetMapping
    public String list(Model model) {

        model.addAttribute("requests", requestRepository.findAll());

        return "manager/request/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {

        Request request = requestRepository.findById(id)
                .orElseThrow();

        model.addAttribute("request", request);

        return "manager/request/details";
    }

    @PostMapping("/documents/{docId}/validate")
    public String validateDoc(@PathVariable Long docId) {
        documentService.validateDocument(docId);
        return "redirect:/requests/requests";
    }

    @PostMapping("/documents/{docId}/reject")
    public String rejectDoc(@PathVariable Long docId,
                            @RequestParam String reason) {
        documentService.rejectDocument(docId, reason);
        return "redirect:/requests/requests";
    }

    @PostMapping("/{id}/approve")
    public String approveRequest(@PathVariable Long id,
                                 RedirectAttributes redirectAttributes) {

        try {

            requestService.approveRequest(id);

            redirectAttributes.addFlashAttribute(
                    "success",
                    "Demande approuvée avec succès"
            );

        } catch (IllegalStateException e) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    e.getMessage()
            );
        }

        return "redirect:/manager/requests/" + id;
    }

    @PostMapping("/{id}/reject")
    public String rejectRequest(@PathVariable Long id) {

        requestService.rejectRequest(id);

        return "redirect:/manager/requests/" + id;
    }
}
