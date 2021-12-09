package com.batch.atm.operator.services.impl;

import com.batch.atm.operator.model.Transaction;
import com.batch.atm.operator.model.sm.ATMEvent;
import com.batch.atm.operator.model.sm.ATMState;
import com.batch.atm.operator.services.ProcessingService;
import com.batch.atm.operator.services.StateMachineService;
import com.batch.atm.operator.services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.StateMachineEventResult;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
public class SessionProcessingService implements ProcessingService {

    @Autowired
    public StateMachineService stateMachineService;

    @Autowired
    public StateMachine<ATMState, ATMEvent> stateMachine;

    @Autowired
    public TransactionService transactionService;

    public SessionProcessingService() {
    }

    @Override
    public Flux<StateMachineEventResult<ATMState, ATMEvent>> processSession() {
        return stateMachineService.sendEvent(ATMEvent.READ_SESSION, stateMachine)
                .flatMap(result -> stateMachineService.sendEvent(transactionService.getUserSession(), ATMEvent.AUTH_EVENT, stateMachine))
                .flatMap(this::processATMRetrieval)
                .flatMap(this::processBalance)
                .map(transaction -> transactionService.rollBackATMRetrieval(transaction))
                .flatMap(result -> stateMachineService.sendEvent(transactionService.getUserSession(), ATMEvent.NEW_TRANSACTION, stateMachine));
    }

    private Flux<Transaction> processATMRetrieval(StateMachineEventResult<ATMState, ATMEvent> result) {
        ATMState state = stateMachine.getState().getId();
        if (state.equals(ATMState.INIT_TRANSACTION)) {
            return Flux.fromStream(transactionService.getUserSession().getTransactions().stream())
                    .flatMap(transaction ->
                            stateMachineService.sendEvent(
                                    transaction,
                                    ATMEvent.PROCESS_TRANSACTION,
                                    stateMachine));
        } else if (state.equals(ATMState.DECLINED_TRANSACTIONS)) {
            return Flux.fromStream(transactionService.getUserSession().getTransactions().stream())
                    .flatMap(transaction ->
                            stateMachineService.sendEvent(
                                    transaction,
                                    ATMEvent.READ_SESSION,
                                    stateMachine));
        }
        return Flux.empty();
    }

    private Flux<Transaction> processBalance(Transaction transaction) {
        ATMState state = stateMachine.getState().getId();
        if (state.equals(ATMState.ATM_READY)) {

            return stateMachineService.sendEvent(
                                    transaction,
                                    ATMEvent.ACCEPT_TRANSACTION,
                                    stateMachine);
        } else if (state.equals(ATMState.ATM_EMPTY)) {
            return stateMachineService.sendEvent(
                    transaction,
                    ATMEvent.NEW_TRANSACTION,
                    stateMachine);
        }
        return Flux.empty();
    }

}
