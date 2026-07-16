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
import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.redis.lettuce.cas.LettuceBasedProxyManager;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.RedisSystemException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder userPasswordEncoder;
    private final EmailService emailService;
    private final VerificationCodeRepository verificationCodeRepository;
    private final LettuceBasedProxyManager<String> proxyManager;


    public AuthService(UserRepository repository,
                       EmailService emailService,
                       VerificationCodeRepository verificationCodeRepository,
                       LettuceBasedProxyManager<String> proxyManager){
        this.userRepository = repository;
        this.emailService = emailService;
        this.verificationCodeRepository = verificationCodeRepository;
        this.proxyManager = proxyManager;
        this.userPasswordEncoder = new BCryptPasswordEncoder();
    }

    public LoginResponseDTO tokenGenerate(@Valid LoginRequestDTO dto) {
        checkEmailRateLimit(dto.email());

        User user = userRepository.findUserByEmailAndActiveStatus(dto.email())
                .orElseThrow(() -> new ApplicationException(ErrorEnum.INVALID_CREDENTIALS));

        if (!this.userPasswordEncoder.matches(dto.password(), user.getPassword())) {
            throw new ApplicationException(ErrorEnum.INVALID_CREDENTIALS);
        }

        return new LoginResponseDTO(UserTokenUtil.createToken(user));
    }

    @Transactional
    public RegisterResponseDTO createUser(RegisterRequestDTO dto){
        checkEmailRateLimit(dto.email());

        if (userRepository.existsUserByEmail(dto.email())) {
            throw new ApplicationException(ErrorEnum.INVALID_CREDENTIALS);
        }

        if (!isValidPassword(dto.password())) {
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

        emailService.sendConfirmationEmail(dto.email(), verificationCode.getCode());

        return new RegisterResponseDTO(
                newUser.getId(),
                newUser.getEmail(),
                "User created. Check your email to confirm your account.");
    }

    @Transactional
    public VerificationCodeResponseDTO verifyCode(VerificationCodeRequestDTO dto) {
        checkEmailRateLimit(dto.email());

        User user = userRepository.findUserByEmail(dto.email())
                .orElseThrow(() -> new ApplicationException(ErrorEnum.RESOURCE_NOT_FOUND));

        VerificationCode verificationCode = verificationCodeRepository.findFirstByUserEmailAndUsedFalseOrderByCreatedAtDesc(dto.email())
                .orElseThrow(() -> new ApplicationException(ErrorEnum.RESOURCE_NOT_FOUND));

        if (!isValidCode(dto.code(), verificationCode)) {
            throw new ApplicationException(ErrorEnum.INVALID_CODE);
        }

        verificationCode.setUsed(true);
        user.setStatus(StatusUser.ACTIVE);

        return new VerificationCodeResponseDTO("User successfully activated.");
    }

    public ForgotPasswordResponseDTO forgotPassword(ForgotPasswordRequestDTO dto) {
        checkEmailRateLimit(dto.email());

        Optional<User> user = userRepository.findUserByEmail(dto.email());

        if (user.isPresent()) {
            VerificationCode verificationCode = this.verificationCodeRepository.save(
                    new VerificationCode(user.get(),
                            OTPGenerator.generate()
                    )
            );

            emailService.sendConfirmationEmail(user.get().getEmail(), verificationCode.getCode());
        }

        return new ForgotPasswordResponseDTO("If an account with this email exists, password reset instructions have been sent.");
    }

    @Transactional
    public ResetPasswordResponseDTO resetPassword(ResetPasswordRequestDTO dto) {
        checkEmailRateLimit(dto.email());

        User user = userRepository.findUserByEmail(dto.email())
                .orElseThrow(() -> new ApplicationException(ErrorEnum.INVALID_CREDENTIALS));

        VerificationCode verificationCode = verificationCodeRepository.findFirstByUserEmailAndUsedFalseOrderByCreatedAtDesc(dto.email())
                .orElseThrow(() -> new ApplicationException(ErrorEnum.RESOURCE_NOT_FOUND));

        if (!isValidCode(dto.code(), verificationCode)) {
            throw new ApplicationException(ErrorEnum.INVALID_CODE);
        }

        if (!isValidPassword(dto.newPassword())) {
            throw new ApplicationException(ErrorEnum.WEAK_PASSWORD);
        }

        user.setPassword(this.userPasswordEncoder.encode(dto.newPassword()));
        verificationCode.setUsed(true);

        return new ResetPasswordResponseDTO("Password reset completed with successfully");
    }

    private boolean isValidCode(String code, VerificationCode verificationCode) {
        return code.equals(verificationCode.getCode())
                && !verificationCode.getExpiresAt().isBefore(LocalDateTime.now());
    }

    private boolean isValidPassword(String password) {
        Strength strength = new Zxcvbn().measure(password);

        return strength.getScore() >= 3;
    }

    private void checkEmailRateLimit(String email) {
        try {
            Bucket bucket = proxyManager.getProxy("rate:login:email:" + email, this::emailLimitConfig);

            if (!bucket.tryConsume(1)) {
                throw new ApplicationException(ErrorEnum.RATE_LIMIT_EXCEEDED);
            }
        } catch (RedisConnectionFailureException | RedisSystemException e) {
            log.warn("Redis unavailable, skipping rate limit. email={}", email, e);
        }
    }

    private BucketConfiguration emailLimitConfig() {
        return BucketConfiguration.builder()
                .addLimit(limit -> limit.capacity(5)
                        .refillGreedy(5,Duration.ofMinutes(15)))
                .build();
    }
}