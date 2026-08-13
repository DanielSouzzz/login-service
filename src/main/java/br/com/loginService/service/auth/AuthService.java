package br.com.loginService.service.auth;

import br.com.loginService.dto.external.*;
import br.com.loginService.dto.internal.VerificationContextDTO;
import br.com.loginService.exception.ApplicationException;
import br.com.loginService.exception.ErrorEnum;
import br.com.loginService.infrastructure.security.ratelimit.EmailRateLimiter;
import br.com.loginService.model.Application;
import br.com.loginService.model.Session;
import br.com.loginService.model.User;
import br.com.loginService.model.VerificationCode;
import br.com.loginService.model.enums.StatusUser;
import br.com.loginService.repository.ApplicationRepository;
import br.com.loginService.repository.UserRepository;
import br.com.loginService.repository.VerificationCodeRepository;
import br.com.loginService.infrastructure.security.OTPGenerator;
import br.com.loginService.service.security.AccessTokenService;
import br.com.loginService.service.email.EmailService;
import com.nulabinc.zxcvbn.Strength;
import com.nulabinc.zxcvbn.Zxcvbn;
import io.github.bucket4j.redis.lettuce.cas.LettuceBasedProxyManager;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
    private final SessionService sessionService;
    private final ApplicationRepository applicationRepository;
    private final EmailRateLimiter emailRateLimiter;


    public AuthService(UserRepository repository,
                       EmailService emailService,
                       VerificationCodeRepository verificationCodeRepository,
                       LettuceBasedProxyManager<String> proxyManager,
                       SessionService sessionService,
                       ApplicationRepository applicationRepository, EmailRateLimiter emailRateLimiter){
        this.userRepository = repository;
        this.emailService = emailService;
        this.verificationCodeRepository = verificationCodeRepository;
        this.proxyManager = proxyManager;
        this.sessionService = sessionService;
        this.applicationRepository = applicationRepository;
        this.emailRateLimiter = emailRateLimiter;
        this.userPasswordEncoder = new BCryptPasswordEncoder();
    }

    public LoginResponseDTO tokenGenerate(@Valid LoginRequestDTO dto, String ip, String authorization) {
        emailRateLimiter.check(dto.email());

        Application application = applicationRepository.
                findApplicationByApiKey(DigestUtils.sha256Hex(authorization))
                .orElseThrow(() -> new ApplicationException(ErrorEnum.INVALID_CREDENTIALS));

        User user = userRepository.findUserByEmailAndActiveStatusAndApplicationId(dto.email(), application.getId())
                .orElseThrow(() -> new ApplicationException(ErrorEnum.INVALID_CREDENTIALS));

        if (!this.userPasswordEncoder.matches(dto.password(), user.getPassword())) {
            throw new ApplicationException(ErrorEnum.INVALID_CREDENTIALS);
        }

        return new LoginResponseDTO(AccessTokenService.createAcessToken(user),
                sessionService.create(user, ip));
    }

    @Transactional
    public RegisterResponseDTO createUser(RegisterRequestDTO dto, String authorization){
        emailRateLimiter.check(dto.email());

        Application application = applicationRepository.
                findApplicationByApiKey(DigestUtils.sha256Hex(authorization))
                .orElseThrow(() -> new ApplicationException(ErrorEnum.INVALID_CREDENTIALS));

        if (userRepository.existsUserByEmail(dto.email())) {
            throw new ApplicationException(ErrorEnum.INVALID_CREDENTIALS);
        }

        if (isInvalidPassword(dto.password())) {
            throw new ApplicationException(ErrorEnum.WEAK_PASSWORD);
        }

        User user = userRepository.
                save(new User(dto.name(),
                        dto.email(),
                        this.userPasswordEncoder.encode(dto.password()),
                        application));

        VerificationCode verificationCode = this.verificationCodeRepository.save(
                new VerificationCode(
                        user,
                        application,
                        OTPGenerator.generate()
                )
        );

        emailService.sendConfirmationEmail(dto.email(), verificationCode.getCode());

        return new RegisterResponseDTO(
                user.getId(),
                user.getEmail(),
                "User created. Check your email to confirm your account.");
    }

    @Transactional
    public VerificationCodeResponseDTO verifyCode(VerificationCodeRequestDTO dto, String authorization) {
        emailRateLimiter.check(dto.email());

        Application application = applicationRepository.
                findApplicationByApiKey(DigestUtils.sha256Hex(authorization))
                .orElseThrow(() -> new ApplicationException(ErrorEnum.INVALID_CREDENTIALS));

        var verificationContextDTO = validateVerificationCode(dto.email(), dto.code(), application.getId());

        verificationContextDTO.verificationCode().setUsed(true);
        verificationContextDTO.user().setStatus(StatusUser.ACTIVE);

        return new VerificationCodeResponseDTO("User successfully activated.");
    }

    public ForgotPasswordResponseDTO forgotPassword(ForgotPasswordRequestDTO dto, String authorization) {
        emailRateLimiter.check(dto.email());

        Application application = applicationRepository.
                findApplicationByApiKey(DigestUtils.sha256Hex(authorization))
                .orElseThrow(() -> new ApplicationException(ErrorEnum.INVALID_CREDENTIALS));

        Optional<User> user = userRepository.findUserByEmailAndApplicationId(dto.email(), application.getId());

        if (user.isPresent()) {
            VerificationCode verificationCode = this.verificationCodeRepository.save(
                    new VerificationCode(user.get(),
                            application,
                            OTPGenerator.generate()
                    )
            );

            emailService.sendConfirmationEmail(user.get().getEmail(), verificationCode.getCode());
        }

        return new ForgotPasswordResponseDTO("If an account with this email exists, password reset instructions have been sent.");
    }

    @Transactional
    public ResetPasswordResponseDTO resetPassword(ResetPasswordRequestDTO dto, String authorization) {
        emailRateLimiter.check(dto.email());

        Application application = applicationRepository.
                findApplicationByApiKey(DigestUtils.sha256Hex(authorization))
                .orElseThrow(() -> new ApplicationException(ErrorEnum.INVALID_CREDENTIALS));

        var verificationContextDTO = validateVerificationCode(dto.email(), dto.code(), application.getId());

        if (isInvalidPassword(dto.newPassword())) {
            throw new ApplicationException(ErrorEnum.WEAK_PASSWORD);
        }

        verificationContextDTO.user().setPassword(this.userPasswordEncoder.encode(dto.newPassword()));
        verificationContextDTO.verificationCode().setUsed(true);

        sessionService.revokeSessions(verificationContextDTO.user().getId());

        return new ResetPasswordResponseDTO("Password reset completed with successfully");
    }

    public RefreshTokenResponseDTO refreshToken(@Valid RefreshTokenRequestDTO dto, String authorization) {

        Application application = applicationRepository.
                findApplicationByApiKey(DigestUtils.sha256Hex(authorization))
                .orElseThrow(() -> new ApplicationException(ErrorEnum.INVALID_CREDENTIALS));

        Session session = sessionService.validate(dto.refresh_token());

        User user = userRepository.findUserByIdAndActiveStatusAndApplicationId(session.getUser().getId(), application.getId())
                .orElseThrow(() -> new ApplicationException(ErrorEnum.INVALID_CREDENTIALS));

        return new RefreshTokenResponseDTO(AccessTokenService.createAcessToken(user),
                sessionService.refreshToken(session.getId()));
    }

    private VerificationContextDTO validateVerificationCode(String email, String code, long applicationId) {
        emailRateLimiter.check(email);

        User user = userRepository.findUserByEmailAndApplicationId(email, applicationId)
                .orElseThrow(() -> new ApplicationException(ErrorEnum.RESOURCE_NOT_FOUND));

        VerificationCode verificationCode = verificationCodeRepository
                .findFirstByUserEmailAndApplicationIdAndUsedFalseOrderByCreatedAtDesc(email, applicationId)
                .orElseThrow(() -> new ApplicationException(ErrorEnum.RESOURCE_NOT_FOUND));

        if (!isValidCode(code, verificationCode)) {
            throw new ApplicationException(ErrorEnum.RESOURCE_NOT_FOUND);
        }

        return new VerificationContextDTO(user, verificationCode);
    }

    private boolean isValidCode(String code, VerificationCode verificationCode) {
        return code.equals(verificationCode.getCode())
                && !verificationCode.getExpiresAt().isBefore(LocalDateTime.now());
    }

    private boolean isInvalidPassword(String password) {
        Strength strength = new Zxcvbn().measure(password);

        return strength.getScore() < 3;
    }
}