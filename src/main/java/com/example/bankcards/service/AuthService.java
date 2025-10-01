package com.example.bankcards.service;

import com.example.bankcards.dto.TokenDTO;
import com.example.bankcards.dto.UserDTO;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.JwtUtil;
import com.example.bankcards.util.RoleUser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public AuthService(AuthenticationManager authenticationManager,
                       PasswordEncoder passwordEncoder,
                       UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    public ResponseEntity<?> login(UserDTO userDTO) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDTO.getUsername(),
                userDTO.getPassword(), Collections.emptyList());
        try {
            Integer userId = userRepository.findIdByUsername(userDTO.getUsername());
            Optional<User> user = userRepository.findById(userId);
            authenticationManager.authenticate(authentication);
            return ResponseEntity.ok(new TokenDTO(new JwtUtil().generateToken(user.get())));
        } catch (AuthenticationException authenticationException) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }

    public ResponseEntity<?> register(UserDTO userDTO) {
        if ((userRepository.countOfUsersWithUsername(userDTO.getUsername()) == 0) &&
                (userDTO.getUsername().length() > 3 && userDTO.getPassword().length() > 3)) {
            userRepository.addUser(userDTO.getUsername(), passwordEncoder.encode(userDTO.getPassword()), String.valueOf(RoleUser.USER));
            userRepository.addUserAuthority(userDTO.getUsername());
            return ResponseEntity.ok("Registration successful");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid credentials or username already exist");
        }
    }
}