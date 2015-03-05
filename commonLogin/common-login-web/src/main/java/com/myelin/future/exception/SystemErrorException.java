package com.myelin.future.exception;

/**
 * Created by gabriel on 14-12-26.
 */
public class SystemErrorException extends RuntimeException {

    public SystemErrorException() {
        super();
    }

    public SystemErrorException(String message, Throwable cause) {
        super(message, cause);
    }

    public SystemErrorException(String message) {
        super(message);
    }

    public SystemErrorException(Throwable cause) {
        super(cause);
    }
}
