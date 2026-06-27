package br.com.loginService.service;

import br.com.loginService.exception.ApplicationException;
import br.com.loginService.exception.ErrorEnum;
import br.com.loginService.model.User;
import br.com.loginService.model.dto.UserDTO;
import br.com.loginService.repository.IUsuario;
import br.com.loginService.service.security.UserToken;
import br.com.loginService.service.security.UserTokenUtil;
import com.nulabinc.zxcvbn.Strength;
import com.nulabinc.zxcvbn.Zxcvbn;
import jakarta.validation.Valid;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final IUsuario userRepository;
    private final PasswordEncoder userPasswordEncoder;

    public AuthService(IUsuario repository){
        this.userRepository = repository;
        this.userPasswordEncoder = new BCryptPasswordEncoder();
    }

    public UserToken tokenGenerate(@Valid UserDTO user) {
        User bd_user = userRepository.findByEmail(user.getEmail());
        if (bd_user != null) {
            boolean valid = userPasswordEncoder.matches(user.getPassword(), bd_user.getPassword());
            if (valid) {
               return new UserToken(UserTokenUtil.createToken(bd_user));
            }
        }
        return null;
    }

    public User createUser(User user){
        if (userRepository.existsUserByEmail(user.getEmail())) {
            throw new ApplicationException(ErrorEnum.INVALID_CREDENTIALS);
        }

        Strength strength = new Zxcvbn().measure(user.getPassword());
        if (strength.getScore() < 3) {
            throw new ApplicationException(ErrorEnum.WEAK_PASSWORD);
        }

        String encoder = this.userPasswordEncoder.encode(user.getPassword());
        user.setPassword(encoder);

//        TODO: Adicionar insert na tabela de verificao de email e enviar code pro user confirmar

        return userRepository.save(user);
    }
}
