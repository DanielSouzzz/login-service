package br.com.loginService.dto;

import jakarta.validation.constraints.Email;

public record ForgotPasswordRequestDTO(
        @Email
        String email
) {
}