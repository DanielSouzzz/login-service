package br.com.loginService.service;

import br.com.loginService.dto.*;
import br.com.loginService.exception.ApplicationException;
import br.com.loginService.exception.ErrorEnum;
import br.com.loginService.mapper.UserMapper;
import br.com.loginService.model.User;
import br.com.loginService.model.VerificationCode;
import br.com.loginService.model.enums.StatusUser;
import br.com.loginService.repository.UserRepository;
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

import java.time.LocalDateTime;

@Slf4j
@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder userPasswordEncoder;
    private final EmailService emailService;
    private final VerificationCodeRepository verificationCodeRepository;

    public AuthService(UserRepository repository, EmailService emailService,
                       VerificationCodeRepository verificationCodeRepository){
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
    public RegisterResponseDTO createUser(RegisterRequestDTO dto){
        if (userRepository.existsUserByEmail(dto.email())) {
            throw new ApplicationException(ErrorEnum.INVALID_CREDENTIALS);
        }

        Strength strength = new Zxcvbn().measure(dto.password());
        if (strength.getScore() < 3) {
            throw new ApplicationException(ErrorEnum.WEAK_PASSWORD);
        }

        User user = UserMapper.toEntity(dto);

        user.setPassword(this.userPasswordEncoder.encode(user.getPassword()));

        User newUser = userRepository.save(user);

        VerificationCode verificationCode = this.verificationCodeRepository.save(
                new VerificationCode(
                        newUser,
                        OTPGenerator.generate()
                )
        );

        try {
            emailService.sendConfirmationEmail(dto.email(), verificationCode.getCode());
        } catch (MessagingException e) {
            throw new ApplicationException(ErrorEnum.EMAIL_SEND_FAILED, e);
        }

        return new RegisterResponseDTO(
                newUser.getId(),
                newUser.getEmail(),
                "User created. Check your email to confirm your account.");
    }

    @Transactional
    public VerificationCodeResponseDTO verifyCode(VerificationCodeRequestDTO dto) {
        User user = userRepository.findById(dto.userId())
                .orElseThrow(() -> new ApplicationException(ErrorEnum.RESOURCE_NOT_FOUND));

        VerificationCode verificationCode = verificationCodeRepository.findByUserIdAndUsedFalse(dto.userId())
                .orElseThrow(() -> new ApplicationException(ErrorEnum.RESOURCE_NOT_FOUND));

        if (!dto.code().equals(verificationCode.getCode())) {
            throw new ApplicationException(ErrorEnum.INVALID_CODE);
        }

        if (verificationCode.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ApplicationException(ErrorEnum.EXPIRED_CODE);
        }

        verificationCode.setUsed(true);
        user.setStatus(StatusUser.ACTIVE);

        return new VerificationCodeResponseDTO("User successfully activated.");
    }
}