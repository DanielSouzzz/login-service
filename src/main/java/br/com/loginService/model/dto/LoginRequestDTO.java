package br.com.loginService.model.dto;

public record LoginRequestDTO(
        String email,
        String password) {
}
