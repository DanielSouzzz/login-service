package br.com.loginService.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorEnum {
    WEAK_PASSWORD("The password is weak, please try again!", HttpStatus.BAD_REQUEST),       // 400
    USER_ALREADY_EXISTS("A user with this email already exists.", HttpStatus.CONFLICT),     // 409
    INVALID_CREDENTIALS("Invalid email or password", HttpStatus.UNAUTHORIZED),              // 401
    RESOURCE_NOT_FOUND("Resource not found", HttpStatus.NOT_FOUND),                         // 404
    USER_DISABLED("User is disabled", HttpStatus.FORBIDDEN),                                // 403
    TOKEN_EXPIRED("Token has expired", HttpStatus.GONE),                                    // 410
    INVALID_TOKEN("Invalid token", HttpStatus.BAD_REQUEST),
    INTERNAL_SERVER_ERROR("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR),
    EMAIL_SEND_FAILED("Error sending confirmation email", HttpStatus.BAD_GATEWAY),
    INVALID_CODE("Invalid verification code", HttpStatus.BAD_REQUEST),
    EXPIRED_CODE("Verification code has expired", HttpStatus.BAD_REQUEST),
    ;

    private final String msg;
    private final HttpStatus status;

    ErrorEnum(String msg, HttpStatus status) {
        this.msg = msg;
        this.status = status;
    }
}