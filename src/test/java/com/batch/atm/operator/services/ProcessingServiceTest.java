package com.batch.atm.operator.services;

import com.batch.atm.operator.model.UserSession;
import com.batch.atm.operator.model.sm.ATMEvent;
import com.batch.atm.operator.model.sm.ATMState;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.action.ReactiveAction;

import java.math.BigDecimal;

import static org.springframework.test.util.AssertionErrors.assertEquals;
import static org.springframework.test.util.AssertionErrors.assertFalse;

@SpringBootTest
class ProcessingServiceTest extends AbstractTransactionServiceTest {

    @Autowired
    StateMachine<ATMState, ATMEvent> stateMachine;

    @Autowired
    TransactionService transactionService;

    @Autowired
    ProcessingService processingService;

    @Autowired
    StateMachineService stateMachineService;

    @Autowired
    private ReactiveAction<ATMState, ATMEvent> verifyAccountBalanceAction;

    @Test
    void processSessionSuccess() {
        //Given
        UserSession session = createSucessTransactionsSession();
        transactionService.setUserSession(session);
        transactionService.setAmount(new BigDecimal(5000));

        //When
        processingService.processSession(session).blockLast();

        //Then
        assertEquals("Amount should be changed", new BigDecimal(3000),transactionService.getAmount());
        assertEquals("User Balance should be changed", new BigDecimal(1000),session.getBalance().getAmount());
        assertFalse("Transaction should not have errors",checkSomeTransactionsHaveErrors(session));
    }
}