package com.example.bankcards.service;

import com.example.bankcards.dto.TokenDTO;
import com.example.bankcards.dto.UserDTO;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.InvalidCredentialsException;
import com.example.bankcards.exception.UserAlreadyExistsException;
import com.example.bankcards.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    private AuthService authService;
    private AuthenticationManager authenticationManager;
    private PasswordEncoder passwordEncoder;
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        authenticationManager = mock(AuthenticationManager.class);
        passwordEncoder = mock(PasswordEncoder.class);
        userRepository = mock(UserRepository.class);
        authService = new AuthService(authenticationManager, passwordEncoder, userRepository);
    }

    @Test
    void login_success() {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("testuser");
        userDTO.setPassword("password");

        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        when(userRepository.findIdByUsername("testuser")).thenReturn(Optional.of(1));
        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken("testuser", "password"));


        ResponseEntity<?> response = authService.login(userDTO);

        assertNotNull(response);
        assertTrue(response.getBody() instanceof TokenDTO);
        verify(authenticationManager, times(1)).authenticate(any());
    }

    @Test
    void login_invalidCredentials() {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("testuser");
        userDTO.setPassword("wrongpass");

        when(userRepository.findIdByUsername("testuser")).thenReturn(Optional.of(1));
        when(userRepository.findById(1)).thenReturn(Optional.of(new User()));
        doThrow(BadCredentialsException.class).when(authenticationManager)
                .authenticate(any(UsernamePasswordAuthenticationToken.class));

        assertThrows(InvalidCredentialsException.class, () -> authService.login(userDTO));
    }

    @Test
    void login_userNotFound() {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("notexist");
        userDTO.setPassword("password");

        when(userRepository.findIdByUsername("notexist")).thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class, () -> authService.login(userDTO));
    }

    @Test
    void register_success() {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("newuser");
        userDTO.setPassword("password");

        when(userRepository.countOfUsersWithUsername("newuser")).thenReturn(0);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");

        ResponseEntity<?> response = authService.register(userDTO);

        assertEquals("Registration successful", response.getBody());
        verify(userRepository, times(1)).addUser("newuser", "encodedPassword", "USER");
        verify(userRepository, times(1)).addUserAuthority("newuser");
    }

    @Test
    void register_userAlreadyExists() {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("existing");
        userDTO.setPassword("password");

        when(userRepository.countOfUsersWithUsername("existing")).thenReturn(1);

        assertThrows(UserAlreadyExistsException.class, () -> authService.register(userDTO));
    }
}
