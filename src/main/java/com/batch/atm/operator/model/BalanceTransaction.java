package com.batch.atm.operator.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
public class BalanceTransaction implements Transaction {
    private final String symbol = "B";
    @Setter
    private BigDecimal balance = new BigDecimal(0);

    @Setter
    private ErrorCode errorCode;

    @Override
    public boolean hasError() {
        return errorCode != null;
    }
}
