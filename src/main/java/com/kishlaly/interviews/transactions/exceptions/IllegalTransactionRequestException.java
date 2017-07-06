package com.kishlaly.interviews.transactions.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author Vladimir Kishlaly
 * @since 06.07.2017
 */
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class IllegalTransactionRequestException extends RuntimeException {

    public IllegalTransactionRequestException() {
    }

    public IllegalTransactionRequestException(String message) {
        super(message);
    }

    public IllegalTransactionRequestException(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalTransactionRequestException(Throwable cause) {
        super(cause);
    }

    public IllegalTransactionRequestException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
