package br.com.loginService.service.application;

import br.com.loginService.dto.external.CreateApplicationRequestDTO;
import br.com.loginService.dto.external.CreateApplicationResponseDTO;
import br.com.loginService.exception.ApplicationException;
import br.com.loginService.exception.ErrorEnum;
import br.com.loginService.model.Application;
import br.com.loginService.repository.ApplicationRepository;
import jakarta.validation.Valid;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;

@Service
public class ApplicationService {
    private final ApplicationRepository applicationRepository;

    public ApplicationService(ApplicationRepository applicationRepository) {
        this.applicationRepository = applicationRepository;
    }

    public CreateApplicationResponseDTO createApplication(@Valid CreateApplicationRequestDTO dto) {
        if (applicationRepository.existsApplicationByOwnerEmail(dto.email())) {
            throw new ApplicationException(ErrorEnum.RESOURCE_ALREADY_EXISTS);
        }

        String apiKey = generateApiKey();

        Application application = new Application(dto.name(),
                dto.html(),
                dto.email(),
                DigestUtils.sha256Hex(apiKey));
        applicationRepository.save(application);

        return new CreateApplicationResponseDTO(application.getId(),
                application.getHtml(),
                application.getOwnerEmail(),
                apiKey);
    }

    private String generateApiKey() {
            byte[] bytes = new byte[64];
            new SecureRandom().nextBytes(bytes);

            return "sk_live_" +
                    Base64.getUrlEncoder()
                            .withoutPadding()
                            .encodeToString(bytes);
    }
}