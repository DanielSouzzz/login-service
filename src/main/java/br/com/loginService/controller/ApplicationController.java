package br.com.loginService.controller;

import br.com.loginService.dto.external.CreateApplicationRequestDTO;
import br.com.loginService.dto.external.CreateApplicationResponseDTO;
import br.com.loginService.service.application.ApplicationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
