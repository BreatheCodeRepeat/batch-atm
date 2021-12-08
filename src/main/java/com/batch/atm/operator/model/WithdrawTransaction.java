package com.batch.atm.operator.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
public class WithdrawTransaction implements Transaction {
    private final String symbol = "W";
    private final BigDecimal amount;
    @Setter
    private BigDecimal balance;

    public WithdrawTransaction(BigDecimal amount) {
        this.amount = amount;
        balance = new BigDecimal(0);
    }

    @Setter
    private ErrorCode errorCode;

    @Override
    public boolean hasError() {
        return errorCode != null;
    }
}
