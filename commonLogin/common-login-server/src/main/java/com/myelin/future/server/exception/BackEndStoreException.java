package com.myelin.future.server.exception;

/**
 * Created by gabriel on 14-12-22.
 */
public class BackEndStoreException extends RuntimeException {

    public BackEndStoreException() {
        super();
    }

    public BackEndStoreException(String message, Throwable cause) {
        super(message, cause);
    }

    public BackEndStoreException(String message) {
        super(message);
    }

    public BackEndStoreException(Throwable cause) {
        super(cause);
    }
}
