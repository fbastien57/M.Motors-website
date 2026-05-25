package com.projetLLD.V1.service;

import com.projetLLD.V1.entity.User;
import com.projetLLD.V1.enums.Role;
import com.projetLLD.V1.repository.UserRepository;
import com.projetLLD.V1.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldRegisterUser() {
        User user = new User();
        user.setPassword("raw");

        when(passwordEncoder.encode("raw")).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        User result = userService.register(user);

        assertEquals(Role.CLIENT, result.getRole());
        assertEquals("encoded", result.getPassword());
    }

    @Test
    void shouldCreateGestionnaire() {
        User user = new User();
        user.setPassword("raw");

        when(passwordEncoder.encode("raw")).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        User result = userService.createGestionnaire(user);

        assertEquals(Role.GESTIONNAIRE, result.getRole());
        assertEquals("encoded", result.getPassword());
    }

    @Test
    void shouldReturnManagers() {
        when(userRepository.findByRole(Role.GESTIONNAIRE))
                .thenReturn(List.of(new User()));

        List<User> result = userService.getAllManagers();

        assertEquals(1, result.size());
        verify(userRepository).findByRole(Role.GESTIONNAIRE);
    }

    @Test
    void shouldDeleteManagerIfRoleCorrect() {
        User user = new User();
        user.setRole(Role.GESTIONNAIRE);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.deleteManager(1L);

        verify(userRepository).delete(user);
    }

    @Test
    void shouldNotDeleteIfNotManager() {
        User user = new User();
        user.setRole(Role.CLIENT);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.deleteManager(1L);

        verify(userRepository, never()).delete(any());
    }

    @Test
    void shouldCheckEmailExists() {
        when(userRepository.existsByEmail("test@test.com")).thenReturn(true);

        assertTrue(userService.emailExists("test@test.com"));
    }

    @Test
    void shouldDeleteUserById() {

        userService.deleteUser(1L);

        verify(userRepository).deleteById(1L);
    }

    @Test
    void shouldThrowExceptionWhenManagerNotFound() {

        when(userRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> userService.deleteManager(1L));

        verify(userRepository, never()).delete(any());
    }
}