package com.budget.backend.controller;

import com.budget.backend.dto.request.LoginRequestDTO;
import com.budget.backend.dto.request.RegisterRequestDTO;
import com.budget.backend.dto.response.AuthResponseDTO;
import com.budget.backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.budget.backend.dto.request.UpdateProfileRequestDTO;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 * AuthController - Controller pentru autentificare
 *
 * @RestController - Combină @Controller + @ResponseBody
 * - @Controller: Marchează clasa ca Spring MVC Controller
 * - @ResponseBody: Returnează JSON în loc de view
 *
 * @RequestMapping("/api/auth") - Prefix pentru toate endpoint-urile
 * Toate endpoint-urile vor începe cu /api/auth
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@Valid @RequestBody RegisterRequestDTO request) {
        AuthResponseDTO response = userService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        AuthResponseDTO response = userService.login(request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/profile")
    public ResponseEntity<AuthResponseDTO> updateProfile(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody UpdateProfileRequestDTO request) {
        AuthResponseDTO response = userService.updateProfile(userId, request);
        return ResponseEntity.ok(response);
    }

}