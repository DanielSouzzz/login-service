package br.com.agendapro.projeto.service;

import br.com.agendapro.projeto.model.User;
import br.com.agendapro.projeto.repository.IUsuario;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private IUsuario repository;

    public UserService(IUsuario repository){
        this.repository = repository;
    }

    public List<User> listUser() {
        List<User> list = repository.findAll();
        return list;
    }

}
