package br.com.loginService.exception;

public class ApplicationException extends RuntimeException {

    public final ErrorEnum error;

    public ApplicationException (ErrorEnum error) {
        super(error.getMsg());
        this.error = error;
    }

    public ApplicationException (ErrorEnum error, Throwable cause) {
        super(error.getMsg(), cause);
        this.error = error;
    }
}
