package com.projetLLD.V1.controller;

import com.projetLLD.V1.dto.VehicleSaleUpdateDTO;
import com.projetLLD.V1.service.VehicleSaleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RequiredArgsConstructor
@Controller
@PreAuthorize("hasAnyRole('GESTIONNAIRE','ADMIN')")
public class VehicleSaleController {

    private final VehicleSaleService vehicleSaleService;

    @GetMapping("/sales/{id}/edit")
    public String editSale(@PathVariable Long id, Model model) {

        model.addAttribute("sale", vehicleSaleService.getSaleForUpdate(id));
        model.addAttribute("saleId", id);

        return "sales/edit";
    }

    @PostMapping("/sales/{id}/update")
    public String updateSale(@PathVariable Long id,
                             @Valid @ModelAttribute("sale") VehicleSaleUpdateDTO dto,
                             BindingResult result) {

        if (result.hasErrors()) {
            return "sales/edit";
        }

        vehicleSaleService.updateSale(id, dto);

        return "redirect:/vehicles";
    }

    @PostMapping("/sales/{id}/delete")
    public String deleteSale(@PathVariable Long id, RedirectAttributes redirectAttributes) {


        try {
            vehicleSaleService.deleteSale(id);
            redirectAttributes.addFlashAttribute("success", "Vente supprimée");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/vehicles";
    }
}
