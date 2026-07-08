package br.com.loginService.dto;

public record VerificationCodeRequestDTO(
        Long userId,
        String code
) {
}