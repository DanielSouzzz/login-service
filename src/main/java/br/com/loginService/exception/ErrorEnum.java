package br.com.loginService.exception;

import lombok.Getter;

@Getter
public enum ErrorEnum {
    WEAK_PASSWORD("The password is weak, please try again!"),
    INVALID_CREDENTIALS( "Invalid username or password"),
    USER_NOT_FOUND("User not found"),
    USER_DISABLED("User is disabled"),
    TOKEN_EXPIRED("Token has expired"),
    INVALID_TOKEN("Invalid token");

    private final String msg;

    ErrorEnum(String msg) {
        this.msg = msg;
    }

}
