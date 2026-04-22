package com.projetLLD.V1.controller;

import com.projetLLD.V1.service.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/public")
public class CatalogController {

    private final VehicleService vehicleService;

    @GetMapping("/sales")
    public String sales(Model model) {
        model.addAttribute("vehicles", vehicleService.getVehiclesForSale());
        return "catalog/sales";
    }

    @GetMapping("/rentals")
    public String rentals(Model model) {
        model.addAttribute("vehicles", vehicleService.getVehiclesForRental());
        return "catalog/rentals";
    }

    @GetMapping("/Details/{id}")
    public String vehicleDetails(@PathVariable Long id, Model model) {

        model.addAttribute("vehicle", vehicleService.getVehicleById(id));

        return "catalog/details";
    }
}
