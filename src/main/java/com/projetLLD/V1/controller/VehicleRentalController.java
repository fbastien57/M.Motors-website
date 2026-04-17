package com.projetLLD.V1.controller;

import com.projetLLD.V1.dto.VehicleRentalCreateDTO;
import com.projetLLD.V1.dto.VehicleRentalUpdateDTO;
import com.projetLLD.V1.entity.VehicleRental;
import com.projetLLD.V1.service.VehicleRentalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class VehicleRentalController {

    private final VehicleRentalService vehicleRentalService;

    @GetMapping("/vehicles/{id}/rentals/new")
    public String showCreateForm(@PathVariable Long id, Model model) {

        model.addAttribute("rental", new VehicleRentalCreateDTO());
        model.addAttribute("vehicleId", id);

        return "rentals/form";
    }

    // CREATE
    @PostMapping("/vehicles/{id}/rentals")
    public String createRental(@PathVariable Long id,
                               @Valid @ModelAttribute("rental") VehicleRentalCreateDTO dto,
                               BindingResult result) {

        if (result.hasErrors()) {
            return "rentals/form";
        }

        vehicleRentalService.createRental(id, dto);

        return "redirect:/vehicles";
    }

    // EDIT FORM
    @GetMapping("/rentals/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {

        VehicleRental rental = vehicleRentalService.getById(id);

        VehicleRentalUpdateDTO dto = new VehicleRentalUpdateDTO();

        dto.setVehiclePrice(rental.getVehiclePrice());
        dto.setDeposit(rental.getDeposit());
        dto.setBaseMonthlyPayment(rental.getBaseMonthlyPayment());
        dto.setAvailable(rental.isAvailable());
        dto.setAllowedDurations(rental.getAllowedDurations());
        dto.setAllowedKmPerYear(rental.getAllowedKmPerYear());

        model.addAttribute("rental", dto);
        model.addAttribute("rentalId", id);

        return "rentals/edit";
    }

    // UPDATE
    @PostMapping("/rentals/{id}/edit")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("rental") VehicleRentalUpdateDTO dto,
                         BindingResult result) {

        if (result.hasErrors()) {
            return "rentals/edit";
        }

        vehicleRentalService.updateRental(id, dto);

        return "redirect:/vehicles";
    }

    // DELETE
    @PostMapping("/rentals/{id}/delete")
    public String delete(@PathVariable Long id) {

        vehicleRentalService.deleteRental(id);

        return "redirect:/vehicles";
    }
}
