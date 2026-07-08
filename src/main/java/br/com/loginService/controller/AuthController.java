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

    private final AuthService userService;

    public AuthController(AuthService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDTO> createUser(@Valid @RequestBody RegisterRequestDTO dto){
        return ResponseEntity.status(201).body(userService.createUser(dto));
    }

    @PostMapping("/verify-code")
    public ResponseEntity<VerificationCodeResponseDTO> verifyCode(@Valid @RequestBody VerificationCodeRequestDTO dto) {
        return ResponseEntity.status(201).body(userService.verifyCode(dto));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> logar(@Valid @RequestBody LoginRequestDTO user){
            return ResponseEntity.ok(userService.tokenGenerate(user));
    }
}
