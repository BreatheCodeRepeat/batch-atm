package com.batch.atm.operator.exceptions;

public class MalformedAmountException extends Exception {

    public MalformedAmountException(String message, Throwable cause) {
        super(message, cause);
    }
}
