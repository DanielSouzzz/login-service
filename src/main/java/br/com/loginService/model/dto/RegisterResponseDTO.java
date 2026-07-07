package br.com.loginService.model.dto;

public record RegisterResponseDTO(
        Long userId,
        String email,
        String msg
) {
}
