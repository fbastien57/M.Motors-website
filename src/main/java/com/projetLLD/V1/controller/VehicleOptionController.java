package com.projetLLD.V1.controller;


import com.projetLLD.V1.dto.VehicleOptionDTO;
import com.projetLLD.V1.service.VehicleOptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/vehicles/{vehicleId}/options")
public class VehicleOptionController {

    private final VehicleOptionService optionService;

    // ✅ LIST
    @GetMapping
    public String list(@PathVariable Long vehicleId, Model model) {
        model.addAttribute("options", optionService.getOptionsByVehicle(vehicleId));
        model.addAttribute("vehicleId", vehicleId);
        return "options/list";
    }

    // ✅ FORM CREATE
    @GetMapping("/new")
    public String createForm(@PathVariable Long vehicleId, Model model) {
        model.addAttribute("option", new VehicleOptionDTO());
        model.addAttribute("vehicleId", vehicleId);
        return "options/create";
    }

    // ✅ CREATE
    @PostMapping
    public String create(
            @PathVariable Long vehicleId,
            @Valid @ModelAttribute("option") VehicleOptionDTO dto,
            BindingResult result) {

        if (result.hasErrors()) {
            return "options/create";
        }

        optionService.createOption(vehicleId, dto);
        return "redirect:/vehicles/" + vehicleId + "/options";
    }

    // ✅ FORM EDIT
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long vehicleId,
                           @PathVariable Long id,
                           Model model) {

        model.addAttribute("option", optionService.getOptionById(id));
        model.addAttribute("vehicleId", vehicleId);
        return "options/edit";
    }

    // ✅ UPDATE
    @PostMapping("/{id}/update")
    public String update(@PathVariable Long vehicleId,
                         @PathVariable Long id,
                         @Valid @ModelAttribute("option") VehicleOptionDTO dto,
                         BindingResult result) {

        if (result.hasErrors()) {
            return "options/edit";
        }

        optionService.updateOption(id, dto);
        return "redirect:/vehicles/" + vehicleId + "/options";
    }

    // ✅ DELETE
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long vehicleId,
                         @PathVariable Long id) {

        optionService.deleteOption(id);
        return "redirect:/vehicles/" + vehicleId + "/options";
    }
}
