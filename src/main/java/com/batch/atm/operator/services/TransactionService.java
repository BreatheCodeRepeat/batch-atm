package com.batch.atm.operator.services;

import com.batch.atm.operator.model.Transaction;
import com.batch.atm.operator.model.UserCredentials;
import com.batch.atm.operator.model.UserSession;

import java.math.BigDecimal;

public interface TransactionService {
    void setUserSession(UserSession session);
    void setAmount(BigDecimal amount);
    BigDecimal getAmount();
    Transaction retrieveAmount(Transaction transaction);
    boolean verifyPin(UserCredentials credentials);
    Transaction verifyAccountBalance(Transaction transaction);
}
