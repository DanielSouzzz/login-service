package br.com.loginService.dto;

public record LoginRequestDTO(
        String email,
        String password) {
}
