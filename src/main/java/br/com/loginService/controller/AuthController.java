package br.com.loginService.controller;

import br.com.loginService.dto.external.*;
import br.com.loginService.service.auth.AuthService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final HttpServletRequest request;


    public AuthController(AuthService userService, HttpServletRequest request) {
        this.authService = userService;
        this.request = request;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDTO> createUser(@Valid @RequestBody RegisterRequestDTO dto,
                                                          @RequestHeader("Authorization") String authorization){
        return ResponseEntity.status(201).body(authService.createUser(dto, authorization));
    }

    @PostMapping("/verify-code")
    public ResponseEntity<VerificationCodeResponseDTO> verifyCode(@Valid @RequestBody VerificationCodeRequestDTO dto,
                                                                  @RequestHeader("Authorization") String authorization) {
        return ResponseEntity.status(201).body(authService.verifyCode(dto, authorization));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> logar(@Valid @RequestBody LoginRequestDTO dto,
                                                  @RequestHeader("Authorization") String authorization) {
            return ResponseEntity.ok(authService.tokenGenerate(dto, request.getRemoteAddr(), authorization));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ForgotPasswordResponseDTO> forgotPassword(@Valid @RequestBody ForgotPasswordRequestDTO dto,
                                                                    @RequestHeader("Authorization") String authorization) {
        return ResponseEntity.ok(authService.forgotPassword(dto, authorization));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ResetPasswordResponseDTO> resetPassword(@Valid @RequestBody ResetPasswordRequestDTO dto,
                                                                  @RequestHeader("Authorization") String authorization) {
        return ResponseEntity.ok(authService.resetPassword(dto, authorization));
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshTokenResponseDTO> refreshToken(@Valid @RequestBody RefreshTokenRequestDTO dto,
                                                                @RequestHeader("Authorization") String authorization) {
        return ResponseEntity.ok(authService.refreshToken(dto, authorization));
    }
}
