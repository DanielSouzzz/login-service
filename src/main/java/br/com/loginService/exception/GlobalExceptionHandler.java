package br.com.loginService.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ErrorResponseDTO> handle (ApplicationException e) {
        ErrorEnum error = e.error;

        log.error("Erro de aplicação: {}", error.getMsg(), e);

        return ResponseEntity
                .status(error.getStatus())
                .body(new ErrorResponseDTO(error.getMsg()));
    }
}