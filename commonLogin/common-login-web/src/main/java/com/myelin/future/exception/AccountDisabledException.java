package com.myelin.future.exception;

/**
 * Created by gabriel on 14-12-18.
 */
public class AccountDisabledException extends RuntimeException {
    private static final long serialVersionUID = 2111967905581320563L;

    public AccountDisabledException() {
        super();
    }

    public AccountDisabledException(String message, Throwable cause) {
        super(message, cause);
    }

    public AccountDisabledException(String message) {
        super(message);
    }

    public AccountDisabledException(Throwable cause) {
        super(cause);
    }
}
