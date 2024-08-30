package br.com.agendapro.projeto.service;

import br.com.agendapro.projeto.model.User;
import br.com.agendapro.projeto.model.dto.UserDTO;
import br.com.agendapro.projeto.repository.IUsuario;
import br.com.agendapro.projeto.service.security.UserToken;
import br.com.agendapro.projeto.service.security.UserTokenUtil;
import jakarta.validation.Valid;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final IUsuario userRepository;
    private final PasswordEncoder userPasswordEncoder;

    public UserService(IUsuario repository){
        this.userRepository = repository;
        this.userPasswordEncoder = new BCryptPasswordEncoder();
    }

    public UserToken tokenGenerate(@Valid UserDTO user) {
        User bd_user = userRepository.findBynameOrEmail(user.getName(), user.getEmail());
        if (bd_user != null) {
            boolean valid = userPasswordEncoder.matches(user.getPassword(), bd_user.getPassword());
            if (valid) {
                return new UserToken(UserTokenUtil.createToken(bd_user));
            }
        }
        return null;
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


}
