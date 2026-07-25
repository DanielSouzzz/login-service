package br.com.loginService.dto.external;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenResponseDTO(
        @NotBlank
        String access_token,
        @NotBlank
        String refresh_token
) {
}
