package br.com.loginService.dto.external;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record VerificationCodeRequestDTO(
        @Email
        @NotBlank
        String email,
        @NotBlank
        String code
) {
}