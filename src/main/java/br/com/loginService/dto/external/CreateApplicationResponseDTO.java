package br.com.loginService.dto.external;

public record CreateApplicationResponseDTO(
        String msg,
        String name,
        String email,
        String api_key
) {
}
