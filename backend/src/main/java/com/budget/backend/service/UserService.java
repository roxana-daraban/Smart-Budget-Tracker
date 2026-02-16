package com.budget.backend.service;

import com.budget.backend.dto.request.LoginRequestDTO;
import com.budget.backend.dto.request.RegisterRequestDTO;
import com.budget.backend.dto.response.AuthResponseDTO;
import com.budget.backend.entity.Role;
import com.budget.backend.entity.User;
import com.budget.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private PasswordEncoder passwordEncoder;


    public AuthResponseDTO register(RegisterRequestDTO request) {
        // 1. Verifică dacă email-ul există deja
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        // 2. Verifică dacă username-ul există deja
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        // 3. Hash-uiește parola cu BCrypt
        String hashedPassword = passwordEncoder.encode(request.getPassword());

        // 4. Creează User entity
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(hashedPassword);
        user.setRole(Role.USER); // Default role

        // 5. Salvează în baza de date
        User savedUser = userRepository.save(user);

        // 6. Generează JWT token
        String token = jwtService.generateToken(savedUser);

        // 7. Returnează AuthResponseDTO
        AuthResponseDTO response = new AuthResponseDTO();
        response.setToken(token);
        response.setUsername(savedUser.getUsername());
        response.setEmail(savedUser.getEmail());
        response.setRole(savedUser.getRole());
        response.setUserId(savedUser.getId());

        return response;
    }
    public AuthResponseDTO login(LoginRequestDTO request) {
        // 1. Găsește utilizatorul după email
        Optional<User> userOptional = userRepository.findByEmail(request.getEmail());

        if (userOptional.isEmpty()) {
            throw new RuntimeException("Invalid email or password");
        }

        User user = userOptional.get();

        // 2. Verifică dacă parola este corectă (compară hash-ul)
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        // 3. Generează JWT token
        String token = jwtService.generateToken(user);

        // 4. Returnează AuthResponseDTO
        AuthResponseDTO response = new AuthResponseDTO();
        response.setToken(token);
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());
        response.setUserId(user.getId());

        return response;
    }
}
