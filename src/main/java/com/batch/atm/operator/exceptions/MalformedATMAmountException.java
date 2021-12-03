package com.batch.atm.operator.exceptions;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class MalformedATMAmountException extends Exception {

    public MalformedATMAmountException(String message, Throwable cause) {
        super(message, cause);
    }
}
