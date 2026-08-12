package br.com.loginService.controller;

import br.com.loginService.dto.external.CreateApplicationRequestDTO;
import br.com.loginService.dto.external.CreateApplicationResponseDTO;
import br.com.loginService.dto.external.VerificationCodeRequestDTO;
import br.com.loginService.dto.external.VerificationCodeResponseDTO;
import br.com.loginService.service.application.ApplicationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/application")
public class ApplicationController {
    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @PostMapping("/create")
    public ResponseEntity<CreateApplicationResponseDTO> createApplication(@Valid @RequestBody CreateApplicationRequestDTO dto) {
        return ResponseEntity.status(201).body(applicationService.createApplication(dto));
    }

    @PostMapping("/confirm-account")
    public ResponseEntity<CreateApplicationResponseDTO> confirmAccount(@Valid @RequestBody VerificationCodeRequestDTO dto) {
        return ResponseEntity.status(201).body(applicationService.confirmAccount(dto));
    }
}
