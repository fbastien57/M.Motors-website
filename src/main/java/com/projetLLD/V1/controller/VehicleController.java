package com.projetLLD.V1.controller;


import com.projetLLD.V1.dto.VehicleRequestDTO;
import com.projetLLD.V1.service.VehicleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/vehicles")
public class VehicleController {

    private final VehicleService vehicleService;

    @GetMapping("/new")
    public String newVehicle(Model model) {
        model.addAttribute("vehicle", new VehicleRequestDTO());
        return "vehicle/newVehicle";
    }

    @PostMapping("/create")
    public String createVehicle(@Valid @ModelAttribute("vehicle") VehicleRequestDTO dto,
                                BindingResult result,
                                Model model) {
        if (result.hasErrors()) return "vehicle/newVehicle";

        try {
            vehicleService.createVehicle(dto);
        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors de la création du véhicule : " + e.getMessage());
            return "vehicle/newVehicle";
        }

        return "redirect:/vehicles";
    }
}
