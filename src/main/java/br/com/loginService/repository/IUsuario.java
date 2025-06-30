package br.com.loginService.repository;

import br.com.loginService.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IUsuario extends JpaRepository<User, Integer> {
    public User findBynameOrEmail(String name, String email);
}
