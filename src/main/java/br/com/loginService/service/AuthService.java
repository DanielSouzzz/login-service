package br.com.loginService.service;

import br.com.loginService.exception.ApplicationException;
import br.com.loginService.exception.ErrorEnum;
import br.com.loginService.model.User;
import br.com.loginService.model.dto.LoginRequestDTO;
import br.com.loginService.model.dto.RegisterRequestDTO;
import br.com.loginService.repository.IUsuario;
import br.com.loginService.model.dto.LoginResponseDTO;
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

    public LoginResponseDTO tokenGenerate(@Valid LoginRequestDTO dto) {
        User user = userRepository.findByEmail(dto.email())
                .orElseThrow(() -> new ApplicationException(ErrorEnum.INVALID_CREDENTIALS));

        if (!this.userPasswordEncoder.matches(dto.password(), user.getPassword())) {
            throw new ApplicationException(ErrorEnum.INVALID_CREDENTIALS);
        }

        return new LoginResponseDTO(UserTokenUtil.createToken(user));
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
