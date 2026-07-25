package br.com.loginService.dto.external;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequestDTO(
        @NotBlank
        String refresh_token
) {
}
