package com.projetLLD.V1.controller;

import com.projetLLD.V1.dto.UserRequestDTO;
import com.projetLLD.V1.entity.User;
import com.projetLLD.V1.service.UserService;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/register")
    public String registerPage(Model model) {

        model.addAttribute("userRequestDTO", new UserRequestDTO());

        return "auth/register";
    }

    @PostMapping("/register")
    public String register(
            @Valid @ModelAttribute("userRequestDTO") UserRequestDTO dto,
            BindingResult result,
            Model model
    ) {

        if(result.hasErrors()) {
            return "auth/register";
        }

        if(userService.emailExists(dto.getEmail())) {

            model.addAttribute(
                    "error",
                    "Cet email est déjà utilisé"
            );

            return "auth/register";
        }

        User user = new User();

        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());

        userService.register(user);

        return "redirect:/login";
    }
}
