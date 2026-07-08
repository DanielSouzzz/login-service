package br.com.loginService.mapper;

import br.com.loginService.dto.RegisterRequestDTO;
import br.com.loginService.dto.RegisterResponseDTO;
import br.com.loginService.model.User;

public class UserMapper {

    public static User toEntity(RegisterRequestDTO dto) {
        return new User(
                dto.name(),
                dto.email(),
                dto.password()
        );
    }

    public static RegisterResponseDTO toDto(User user) {
        return new RegisterResponseDTO(
                user.getId(),
                user.getEmail(),
                user.getPassword()
        );
    }
}