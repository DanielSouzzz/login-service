package br.com.loginService.model.dto;

public record VerificationCodeRequestDTO(
        Long userId,
        String code
) {
}