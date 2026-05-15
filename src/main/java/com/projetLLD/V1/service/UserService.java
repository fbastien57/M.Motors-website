package com.projetLLD.V1.service;

import com.projetLLD.V1.entity.User;
import com.projetLLD.V1.enums.Role;
import com.projetLLD.V1.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User register(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.CLIENT); // par défaut
        return userRepository.save(user);
    }

    public User createGestionnaire(User user) {

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        user.setRole(Role.GESTIONNAIRE);

        return userRepository.save(user);
    }

    public List<User> getAllManagers() {
        return userRepository.findByRole(Role.GESTIONNAIRE);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public void deleteManager(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow();

        if(user.getRole() == Role.GESTIONNAIRE) {
            userRepository.delete(user);
        }
    }

    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

}
