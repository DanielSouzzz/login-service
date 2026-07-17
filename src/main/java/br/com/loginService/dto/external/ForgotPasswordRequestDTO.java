package br.com.loginService.dto.external;

import jakarta.validation.constraints.Email;

public record ForgotPasswordRequestDTO(
        @Email
        String email
) {
}