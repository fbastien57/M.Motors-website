package com.projetLLD.V1.controller;


import com.projetLLD.V1.dto.VehicleRequestDTO;
import com.projetLLD.V1.entity.Vehicle;
import com.projetLLD.V1.service.VehicleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/vehicles")
public class VehicleController {

    private final VehicleService vehicleService;

    @GetMapping
    public String listVehicles(Model model) {
        List<Vehicle> vehicles = vehicleService.getAllVehicles();
        model.addAttribute("vehicles", vehicles);
        return "vehicle/list";
    }

    @GetMapping("/new")
    public String newVehicle(Model model) {
        System.out.println(System.getProperty("spring.servlet.multipart.max-file-size"));
        model.addAttribute("vehicle", new VehicleRequestDTO());
        return "vehicle/newVehicle";
    }

    @PostMapping("/create")
    public String createVehicle(
            @Valid @ModelAttribute("vehicle") VehicleRequestDTO dto,
            BindingResult result,
            Model model) {

        if (result.hasErrors()) return "vehicle/newVehicle";

        Vehicle vehicle = vehicleService.createVehicle(dto); // ✅ SANS photos

        return "redirect:/vehicles/" + vehicle.getId() + "/edit";
    }

    @GetMapping("/{id}/edit")
    public String editVehicle(@PathVariable Long id, Model model) {
        model.addAttribute("vehicle", vehicleService.getVehicleById(id));
        return "vehicle/editVehicle";
    }

    @PostMapping("/update/{id}")
    public String updateVehicle(
            @PathVariable Long id,
            @ModelAttribute VehicleRequestDTO dto) {

        vehicleService.updateVehicle(id, dto);
        return "redirect:/vehicles/" + id + "/edit";
    }

    @PostMapping("/{id}/delete")
    public String deleteVehicle(@PathVariable Long id) {

        vehicleService.deleteVehicle(id);
        return "redirect:/vehicles";
    }

    @PostMapping("/{id}/upload-images")
    public String uploadImages(
            @PathVariable Long id,
            @RequestParam("files") MultipartFile[] files,
            Model model) {

        try {
            vehicleService.addImages(id, files);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }

        return "redirect:/vehicles/" + id + "/edit";
    }

    @PostMapping("/{vehicleId}/photos/{photoId}/delete")
    public String deletePhoto(
            @PathVariable Long vehicleId,
            @PathVariable Long photoId) {

        vehicleService.deletePhoto(vehicleId, photoId);

        return "redirect:/vehicles/" + vehicleId + "/edit";
    }
}
