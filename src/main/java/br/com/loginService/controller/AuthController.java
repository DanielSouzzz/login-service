package br.com.loginService.controller;

import br.com.loginService.model.User;
import br.com.loginService.model.dto.UserDTO;
import br.com.loginService.service.AuthService;

import br.com.loginService.service.security.UserToken;
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
    public ResponseEntity<User> createUser(@Valid @RequestBody User user){
        return ResponseEntity.status(201).body(userService.createUser(user));
    }

    @PostMapping("/login")
    public ResponseEntity<UserToken> logar(@Valid @RequestBody UserDTO user){
        UserToken token = userService.tokenGenerate(user);
        if(token != null) {
            return ResponseEntity.ok(token);
        }
        return ResponseEntity.status(403).build();
    }
}
