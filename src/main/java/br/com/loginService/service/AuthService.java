package br.com.loginService.service;

import br.com.loginService.exception.ApplicationException;
import br.com.loginService.exception.ErrorEnum;
import br.com.loginService.model.User;
import br.com.loginService.model.VerificationCode;
import br.com.loginService.model.dto.LoginRequestDTO;
import br.com.loginService.model.dto.RegisterResponseDTO;
import br.com.loginService.repository.UserRepository;
import br.com.loginService.model.dto.LoginResponseDTO;
import br.com.loginService.repository.VerificationCodeRepository;
import br.com.loginService.security.OTPGenerator;
import br.com.loginService.security.UserTokenUtil;
import com.nulabinc.zxcvbn.Strength;
import com.nulabinc.zxcvbn.Zxcvbn;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder userPasswordEncoder;
    private final EmailService emailService;
    private final VerificationCodeRepository verificationCodeRepository;

    public AuthService(UserRepository repository, EmailService emailService, VerificationCodeRepository verificationCodeRepository){
        this.userRepository = repository;
        this.emailService = emailService;
        this.verificationCodeRepository = verificationCodeRepository;
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

    @Transactional
    public RegisterResponseDTO createUser(User user){
        if (userRepository.existsUserByEmail(user.getEmail())) {
            throw new ApplicationException(ErrorEnum.INVALID_CREDENTIALS);
        }

        Strength strength = new Zxcvbn().measure(user.getPassword());
        if (strength.getScore() < 3) {
            throw new ApplicationException(ErrorEnum.WEAK_PASSWORD);
        }

        user.setPassword(this.userPasswordEncoder.encode(user.getPassword()));

        User newUser = userRepository.save(user);

        VerificationCode verificationCode = this.verificationCodeRepository.save(
                new VerificationCode(
                        newUser,
                        OTPGenerator.generate()
                )
        );

        try {
            emailService.sendConfirmationEmail(user.getEmail(), verificationCode.getCode());
        } catch (MessagingException e) {
            throw new ApplicationException(ErrorEnum.EMAIL_SEND_FAILED, e);
        }

        return new RegisterResponseDTO(
                newUser.getId(),
                newUser.getEmail(),
                "User creted. Check your email to confirm your account.");
    }
}
