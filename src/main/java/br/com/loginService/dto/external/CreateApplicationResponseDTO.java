package br.com.loginService.dto.external;

public record CreateApplicationResponseDTO(
        Long id,
        String name,
        String email,
        String api_key
) {
}
