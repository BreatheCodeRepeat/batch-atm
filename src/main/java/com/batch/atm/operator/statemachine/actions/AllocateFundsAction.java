package com.batch.atm.operator.statemachine.actions;

import com.batch.atm.operator.model.ErrorCode;
import com.batch.atm.operator.model.Transaction;
import com.batch.atm.operator.model.sm.ATMEvent;
import com.batch.atm.operator.model.sm.ATMState;
import com.batch.atm.operator.services.StateMachineService;
import com.batch.atm.operator.services.TransactionService;
import com.batch.atm.operator.services.impl.ATMStateMachineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.ReactiveAction;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class AllocateFundsAction implements ReactiveAction<ATMState, ATMEvent> {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private StateMachineService stateMachineService;

    @Override
    public Mono<Void> apply(StateContext<ATMState, ATMEvent> stateContext) {
        Transaction transaction = (Transaction) stateContext.getMessageHeader(
                ATMStateMachineService.TRANSACTION_HEADER
        );
        return processTransactions(transaction, stateContext);
    }

    private Mono<Void> processTransactions(Transaction transaction, StateContext<ATMState, ATMEvent> stateContext) {
        Transaction transactionResult = transactionService.retrieveAmount(transaction);
        if(transactionResult.hasError() && transactionResult.getErrorCode().equals(ErrorCode.ATM_ERR)){
            return stateMachineService.sendEvent(ATMEvent.NOT_ENOUGHT_CASH,stateContext.getStateMachine())
                    .then();
        }
        return stateMachineService.sendEvent(ATMEvent.ATM_HAS_CASH,stateContext.getStateMachine())
                .then();
    }
}
