package br.com.loginService.repository;

import br.com.loginService.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IUsuario extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);

    boolean existsUserByEmail(String email);
}
