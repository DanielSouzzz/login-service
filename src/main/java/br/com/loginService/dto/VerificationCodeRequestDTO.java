package br.com.loginService.dto;

public record VerificationCodeRequestDTO(
        String email,
        String code
) {
}