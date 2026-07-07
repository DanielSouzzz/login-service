package br.com.loginService.controller;

import br.com.loginService.model.User;
import br.com.loginService.model.dto.LoginRequestDTO;
import br.com.loginService.model.dto.RegisterRequestDTO;
import br.com.loginService.model.dto.RegisterResponseDTO;
import br.com.loginService.service.AuthService;

import br.com.loginService.model.dto.LoginResponseDTO;
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
    public ResponseEntity<RegisterResponseDTO> createUser(@Valid @RequestBody User user){
        return ResponseEntity.status(201).body(userService.createUser(user));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> logar(@Valid @RequestBody LoginRequestDTO user){
            return ResponseEntity.ok(userService.tokenGenerate(user));
    }
}
