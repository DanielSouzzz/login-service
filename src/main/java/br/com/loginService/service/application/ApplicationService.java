package br.com.loginService.service.application;

import br.com.loginService.dto.external.CreateApplicationRequestDTO;
import br.com.loginService.dto.external.CreateApplicationResponseDTO;
import br.com.loginService.dto.external.VerificationCodeRequestDTO;
import br.com.loginService.exception.ApplicationException;
import br.com.loginService.exception.ErrorEnum;
import br.com.loginService.infrastructure.security.OTPGenerator;
import br.com.loginService.infrastructure.security.ratelimit.EmailRateLimiter;
import br.com.loginService.model.Application;
import br.com.loginService.model.VerificationCode;
import br.com.loginService.repository.ApplicationRepository;
import br.com.loginService.repository.VerificationCodeRepository;
import br.com.loginService.service.email.EmailService;
import jakarta.validation.Valid;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
public class ApplicationService {
    private final ApplicationRepository applicationRepository;
    private final VerificationCodeRepository verificationCodeRepository;
    private final EmailService emailService;
    private final EmailRateLimiter emailRateLimiter;


    public ApplicationService(ApplicationRepository applicationRepository, VerificationCodeRepository verificationCodeRepository, EmailService emailService, EmailRateLimiter emailRateLimiter) {
        this.applicationRepository = applicationRepository;
        this.verificationCodeRepository = verificationCodeRepository;
        this.emailService = emailService;
        this.emailRateLimiter = emailRateLimiter;
    }

    public CreateApplicationResponseDTO createApplication(@Valid CreateApplicationRequestDTO dto) {
        emailRateLimiter.check(dto.email());

        if (applicationRepository.existsApplicationByOwnerEmail(dto.email())) {
            throw new ApplicationException(ErrorEnum.INVALID_CREDENTIALS);
        }

        Application application = new Application(dto.name(),
                dto.html(),
                dto.email(),
                false);
        applicationRepository.save(application);

        VerificationCode verificationCode = this.verificationCodeRepository.save(
                new VerificationCode(
                        null,
                        application,
                        OTPGenerator.generate()
                )
        );

        emailService.sendConfirmationEmail(application.getOwnerEmail(), verificationCode.getCode());

        return new CreateApplicationResponseDTO(
                "Application created. Check your email to confirm your account and get your apiKey",
                application.getName(),
                application.getOwnerEmail(),
                null);
    }

    public CreateApplicationResponseDTO confirmAccount(@Valid VerificationCodeRequestDTO dto) {
        emailRateLimiter.check(dto.email());

        Application application = applicationRepository.
                findApplicationByOwnerEmail(dto.email())
                .orElseThrow(() -> new ApplicationException(ErrorEnum.INVALID_CREDENTIALS));

        VerificationCode verificationCode = verificationCodeRepository
                .findFirstByApplicationOwnerEmailAndUsedFalseOrderByCreatedAtDesc(dto.email())
                .orElseThrow(() -> new ApplicationException(ErrorEnum.INVALID_CREDENTIALS));

        if (!(verificationCode.getCode().equals(dto.code()))
                || verificationCode.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ApplicationException(ErrorEnum.INVALID_CREDENTIALS);
        }

        String apiKey = generateApiKey();

        application.setApiKey(DigestUtils.sha256Hex(apiKey));
        application.setEnabled(true);
        this.applicationRepository.save(application);

        return new CreateApplicationResponseDTO(
                "Congratulations! Application successfully activated.",
                application.getName(),
                application.getOwnerEmail(),
                apiKey
        );
    }

    private String generateApiKey() {
            byte[] bytes = new byte[255];
            new SecureRandom().nextBytes(bytes);

            return "sk_live_" +
                    Base64.getUrlEncoder()
                            .withoutPadding()
                            .encodeToString(bytes);
    }

}