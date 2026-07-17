package br.com.loginService.dto.internal;

import br.com.loginService.model.User;
import br.com.loginService.model.VerificationCode;

public record VerificationContextDTO(
        User user,
        VerificationCode verificationCode
) {
}
