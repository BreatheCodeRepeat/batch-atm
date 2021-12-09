package com.batch.atm.operator.services;

import com.batch.atm.operator.model.ErrorCode;
import com.batch.atm.operator.model.Transaction;
import com.batch.atm.operator.model.UserSession;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.springframework.test.util.AssertionErrors.assertEquals;
import static org.springframework.test.util.AssertionErrors.assertFalse;
import static org.springframework.test.util.AssertionErrors.assertTrue;

@SpringBootTest
class TransactionServiceTest extends AbstractTransactionServiceTest {

    @Autowired
    TransactionService transactionService;

    @Test
    void rollBackATMRetrievalTransaction() {
        //Given
        UserSession session = createATMRetrievalRollBackSession();

        //When
        BigDecimal initialAmount = new BigDecimal(1000);
        transactionService.setAmount(initialAmount);
        Transaction transaction = transactionService.retrieveAmount(session.getTransactions().get(0));

        //Then
        assertEquals("Amount should be left unchanged because of roll back",
                new BigDecimal(1000),
                transactionService.getAmount());
    }

    @Test
    void sucessTransaction() {
        //Given
        UserSession session = createSucessTransactionsSession();

        //When
        BigDecimal initialAmount = new BigDecimal(5000);
        transactionService.setAmount(initialAmount);
        transactionService.setUserSession(session);
        Transaction transaction = transactionService.retrieveAmount(session.getTransactions().get(0));
        transaction = transactionService.verifyAccountBalance(transaction);

        //Then
        assertEquals("Amount should be changed", new BigDecimal(3000),transactionService.getAmount());
        assertEquals("User Balance should be changed", new BigDecimal(1000),session.getBalance().getAmount());
        assertFalse("Transaction should not have errors",transaction.hasError());
    }

    @Test
    void checkPINError() {
        //Given
        UserSession session = createWrongPINMockSession();

        //When
        transactionService.setUserSession(session);
        boolean loggedIn = transactionService.verifyPin(session.getCredentials());

        //Then
        assertFalse("Wrong pin, it should be false",loggedIn);
    }

    @Test
    void rollBackAccountBalance() {
        //Given
        UserSession session = createRoolbackTransactionsSession();

        //When
        BigDecimal initialAmount = new BigDecimal(5000);
        transactionService.setAmount(initialAmount);
        transactionService.setUserSession(session);
        Transaction transaction = transactionService.verifyAccountBalance(session.getTransactions().get(0));

        //Then
        assertEquals("Amount should not be changed", new BigDecimal(5000),transactionService.getAmount());
        assertEquals("User Balance should not be changed", new BigDecimal(1000),session.getBalance().getAmount());
        assertTrue("Transaction should have erros",transaction.hasError());
        assertEquals("Transaction error should be FUND ERROR", ErrorCode.FUNDS_ERR,transaction.getErrorCode());
    }

    @Test
    void should_succesTransaction_With_Overdraft_And_Zero_Balance() {
        //Given
        UserSession session = createZeroBalanceOverdraftTransactionsSession();

        //When
        BigDecimal initialAmount = new BigDecimal(5000);
        transactionService.setAmount(initialAmount);
        transactionService.setUserSession(session);
        Transaction transaction = transactionService.retrieveAmount(session.getTransactions().get(0));
        transaction = transactionService.verifyAccountBalance(transaction);

        //Then
        assertEquals("Amount should be changed", new BigDecimal(3900),transactionService.getAmount());
        assertEquals("User Balance should be changed", new BigDecimal(0),session.getBalance().getAmount());
        assertEquals("User Overdraft limit should be changed",
                new BigDecimal(400),
                session.getBalance().getOverdraft());
        assertFalse("Transaction should not have errors",transaction.hasError());
    }

}