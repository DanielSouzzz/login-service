package br.com.loginService.exception;

public class ApplicationException extends RuntimeException {
    public ApplicationException (ErrorEnum error) {
        super(error.getMsg());
    }

    public ApplicationException (ErrorEnum error, Throwable cause) {
        super(error.getMsg(), cause);
    }
}
