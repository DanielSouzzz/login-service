package br.com.loginService.controller;

import br.com.loginService.dto.*;
import br.com.loginService.service.AuthService;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService userService) {
        this.authService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDTO> createUser(@Valid @RequestBody RegisterRequestDTO dto){
        return ResponseEntity.status(201).body(authService.createUser(dto));
    }

    @PostMapping("/verify-code")
    public ResponseEntity<VerificationCodeResponseDTO> verifyCode(@Valid @RequestBody VerificationCodeRequestDTO dto) {
        return ResponseEntity.status(201).body(authService.verifyCode(dto));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> logar(@Valid @RequestBody LoginRequestDTO user){
            return ResponseEntity.ok(authService.tokenGenerate(user));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ForgotPasswordResponseDTO> forgotPassword(@RequestBody ForgotPasswordRequestDTO dto) {
        return ResponseEntity.ok(authService.forgotPassword(dto));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ResetPasswordResponseDTO> resetPassword(@RequestBody ResetPasswordRequestDTO dto) {
        return ResponseEntity.ok(authService.resetPassword(dto));
    }
}
