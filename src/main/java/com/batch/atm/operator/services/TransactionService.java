package com.batch.atm.operator.services;

import java.math.BigDecimal;

public interface TransactionService {
    void setInitialAmount(BigDecimal amount);
}
