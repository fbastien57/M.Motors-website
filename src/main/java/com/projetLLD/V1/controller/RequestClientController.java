package com.projetLLD.V1.controller;

import com.projetLLD.V1.config.UserPrincipal;
import com.projetLLD.V1.dto.CreateRequestDTO;
import com.projetLLD.V1.entity.Request;
import com.projetLLD.V1.entity.User;
import com.projetLLD.V1.entity.Vehicle;
import com.projetLLD.V1.exception.VehicleNotFoundException;
import com.projetLLD.V1.repository.VehicleRepository;
import com.projetLLD.V1.service.RequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/client/requests")
@RequiredArgsConstructor
public class RequestClientController {

    private final RequestService requestService;
    private final VehicleRepository vehicleRepository;

    @GetMapping
    public String myRequests(Model model,
                             @AuthenticationPrincipal UserPrincipal principal) {

        User user = principal.getUser();

        model.addAttribute("requests",
                requestService.getUserRequests(user.getId()));

        return "requests/list";
    }

    @GetMapping("/{id}")
    public String requestDetails(@PathVariable Long id,
                                 @AuthenticationPrincipal UserPrincipal principal,
                                 Model model) {

        User user = principal.getUser();

        Request request = requestService.getRequestForUser(id, user.getId());

        model.addAttribute("request", request);

        return "requests/details";
    }

    @GetMapping("/create/sale/{vehicleId}")
    public String showSaleRequest(@PathVariable Long vehicleId, Model model) {

        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new VehicleNotFoundException("Vehicle not found"));

        model.addAttribute("vehicle", vehicle);

        return "requests/create-sale-request";
    }

    @GetMapping("/create/rental/{vehicleId}")
    public String showRentalRequest(@PathVariable Long vehicleId, Model model) {

        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow();

        model.addAttribute("vehicle", vehicle);

        return "requests/create-rental-request";
    }

    @PostMapping("/create")
    public String create(@ModelAttribute CreateRequestDTO dto,
                         @AuthenticationPrincipal UserPrincipal principal) {

        User user = principal.getUser();

        requestService.createRequest(dto, user);

        return "redirect:/dashboard";
    }
}
