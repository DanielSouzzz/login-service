package br.com.agendapro.projeto.DAO;

import br.com.agendapro.projeto.model.User;
import org.springframework.data.repository.CrudRepository;

public interface IUsuario extends CrudRepository<User, Integer> {
}
