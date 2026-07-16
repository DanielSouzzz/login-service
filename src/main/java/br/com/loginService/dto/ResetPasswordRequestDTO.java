package br.com.loginService.dto;

public record ResetPasswordRequestDTO(
        String email,
        String code,
        String newPassword
) {
}
