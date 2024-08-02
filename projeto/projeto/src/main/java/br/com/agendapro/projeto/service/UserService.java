package br.com.agendapro.projeto.service;

import br.com.agendapro.projeto.model.User;
import br.com.agendapro.projeto.repository.IUsuario;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private IUsuario userRepository;
    private PasswordEncoder userPasswordEncoder;

    public UserService(IUsuario repository){
        this.userRepository = repository;
        this.userPasswordEncoder = new BCryptPasswordEncoder();
    }

    public List<User> listUser() {
        List<User> listUser = userRepository.findAll();
        return listUser;
    }

    public User createUser(User user){
        String encoder = this.userPasswordEncoder.encode(user.getPassword());
        user.setPassword(encoder);
        User newUser = userRepository.save(user);
        return newUser;
    }

    public User editUser(User user){
        String encoder = this.userPasswordEncoder.encode(user.getPassword());
        user.setPassword(encoder);
        User editedUser = userRepository.save(user);
        return editedUser;
    }

    public Boolean deleteUser(Integer id){
        if (!userRepository.existsById(id)) {
            return false;
        }
        userRepository.deleteById(id);
        return true;
    }

    public boolean validatePassword (User user){
        String passwordUser = userRepository.getById(user.getId()).getPassword();
        Boolean validate = userPasswordEncoder.matches(user.getPassword(), passwordUser);
        return validate;
    }
}
