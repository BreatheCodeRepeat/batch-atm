package com.batch.atm.operator.services;

import com.batch.atm.operator.model.Transaction;
import com.batch.atm.operator.model.UserCredentials;
import com.batch.atm.operator.model.UserSession;

import java.math.BigDecimal;

public interface TransactionService {
    void setUserSession(UserSession session);
    void setAmount(BigDecimal amount);
    Transaction retrieveAmount(Transaction transaction,UserSession session);
    boolean verifyPin(UserCredentials credentials);
    Transaction verifyAccountBalance(Transaction transaction);
    void revertTransaction(Transaction transaction,UserSession session);
}
