package br.com.loginService.repository;

import br.com.loginService.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IUsuario extends JpaRepository<User, Integer> {
    User findByEmail(String email);

    boolean existsUserByEmail(String email);
}
