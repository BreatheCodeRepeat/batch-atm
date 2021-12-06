package com.batch.atm.operator.services;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class ATMTransactionService implements TransactionService {

    private BigDecimal amount;

    @Override
    public synchronized void setInitialAmount(BigDecimal amount) {
        if(this.amount == null){
            this.amount = amount;
        }
    }
}
