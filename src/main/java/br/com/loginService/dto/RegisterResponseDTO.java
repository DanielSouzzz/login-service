package br.com.loginService.dto;

public record RegisterResponseDTO(
        Long userId,
        String email,
        String msg
) {
}
