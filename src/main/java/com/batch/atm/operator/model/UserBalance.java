package com.batch.atm.operator.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class UserBalance {
    private BigDecimal amount;
    private BigDecimal overdraft;
}
