package com.myelin.future.exception;

/**
 * Created by gabriel on 15-1-5.
 */
public class NotSupportLoginTypeException extends RuntimeException {
    public NotSupportLoginTypeException() {
        super();
    }

    public NotSupportLoginTypeException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotSupportLoginTypeException(String message) {
        super(message);
    }

    public NotSupportLoginTypeException(Throwable cause) {
        super(cause);
    }
}
