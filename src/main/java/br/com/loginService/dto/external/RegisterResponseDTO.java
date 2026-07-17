package br.com.loginService.dto.external;

public record RegisterResponseDTO(
        Long userId,
        String email,
        String msg
) {
}
