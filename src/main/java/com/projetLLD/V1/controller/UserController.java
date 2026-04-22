package com.projetLLD.V1.controller;

import com.projetLLD.V1.dto.UserRequestDTO;
import com.projetLLD.V1.entity.User;
import com.projetLLD.V1.service.UserService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/register")
    public String registerPage() {
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(UserRequestDTO dto) {

        User user = new User();
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());

        userService.register(user);

        return "redirect:/login";
    }
}
