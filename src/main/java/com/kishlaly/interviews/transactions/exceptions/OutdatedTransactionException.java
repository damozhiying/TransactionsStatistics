package com.kishlaly.interviews.transactions.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author Vladimir Kishlaly
 * @since 06.07.2017
 */
@ResponseStatus(value = HttpStatus.NO_CONTENT)
public class OutdatedTransactionException extends RuntimeException {

    public OutdatedTransactionException() {
    }

    public OutdatedTransactionException(String message) {
        super(message);
    }

    public OutdatedTransactionException(String message, Throwable cause) {
        super(message, cause);
    }

    public OutdatedTransactionException(Throwable cause) {
        super(cause);
    }

    public OutdatedTransactionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
