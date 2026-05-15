package com.projetLLD.V1.controller;

import com.projetLLD.V1.dto.UserRequestDTO;
import com.projetLLD.V1.entity.User;
import com.projetLLD.V1.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN')")
public class AdminController {

    private final UserService userService;

    // PAGE CREATION
    @GetMapping("/admin/create-manager")
    public String createManagerPage() {
        return "admin/create-manager";
    }

    // CREATE MANAGER
    @PostMapping("/admin/create-manager")
    public String createManager(UserRequestDTO dto) {

        User user = new User();

        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());

        userService.createGestionnaire(user);

        return "redirect:/admin/managers";
    }

    // LISTE GESTIONNAIRES
    @GetMapping("/admin/managers")
    public String managersPage(Model model) {

        List<User> managers = userService.getAllManagers();

        model.addAttribute("managers", managers);

        return "admin/manager";
    }

    // DELETE
    @PostMapping("/admin/delete-manager/{id}")
    public String deleteManager(@PathVariable Long id) {

        userService.deleteManager(id);

        return "redirect:/admin/managers";
    }
}
