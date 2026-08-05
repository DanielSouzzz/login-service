package br.com.loginService.dto.external;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreateApplicationRequestDTO(
        @NotBlank
        String name,
        @NotBlank
        @Email
        String email,
        String html
) {
}
