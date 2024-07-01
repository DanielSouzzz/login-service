package br.com.agendapro.projeto.repository;

import br.com.agendapro.projeto.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IUsuario extends JpaRepository<User, Integer> {
}
