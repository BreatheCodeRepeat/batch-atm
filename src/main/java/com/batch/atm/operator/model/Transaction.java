package com.batch.atm.operator.model;

import java.math.BigDecimal;

public interface Transaction {
    String getSymbol();

    BigDecimal getBalance();

    void setBalance(BigDecimal balance);

    boolean hasError();

    ErrorCode getErrorCode();

    void setErrorCode(ErrorCode errorCode);
}
