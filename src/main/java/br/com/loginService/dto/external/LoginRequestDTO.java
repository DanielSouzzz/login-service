package br.com.loginService.dto.external;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LoginRequestDTO(
        @Email
        String email,
        @NotNull
        @NotBlank
        String password) {
}
