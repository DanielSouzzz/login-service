package br.com.agendapro.projeto.service;

import br.com.agendapro.projeto.model.User;
import br.com.agendapro.projeto.model.dto.UserDTO;
import br.com.agendapro.projeto.repository.IUsuario;
import br.com.agendapro.projeto.service.security.UserToken;
import br.com.agendapro.projeto.service.security.UserTokenUtil;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final IUsuario userRepository;
    private final PasswordEncoder userPasswordEncoder;
    private final Logger loggerUser = LoggerFactory.getLogger(UserService.class);

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
        loggerUser.info("Listing all users. Action performed by: {}", getLoggedUser());
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

    public Boolean deleteUser(Integer idUser){
        if (!userRepository.existsById(idUser)) {
            loggerUser.warn("Attempt to delete no existing user with id: {}. Action done by {}", idUser, getLoggedUser());
            return false;
        }
        userRepository.deleteById(idUser);
        loggerUser.info("User with id {} has been deleted. Action done by {}", idUser, getLoggedUser());
        return true;
    }

    // logs para a aplicacao
    private String getLoggedUser(){
        Authentication loggedUser = SecurityContextHolder.getContext().getAuthentication();
        if(!(loggedUser instanceof User AnonymousAuthenticationToken)){
            return loggedUser.getName();
        }
        return null;
    }
}
