package br.com.loginService.dto.external;

public record LoginResponseDTO(
        String accessToken,
        String refreshToken) {
}