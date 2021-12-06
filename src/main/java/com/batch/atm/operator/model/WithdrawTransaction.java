package com.batch.atm.operator.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class WithdrawTransaction implements Transaction {
    private final String symbol = "W";
    private BigDecimal amount;
}
