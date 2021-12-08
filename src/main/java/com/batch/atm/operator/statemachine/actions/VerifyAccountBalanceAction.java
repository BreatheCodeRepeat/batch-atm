package com.batch.atm.operator.statemachine.actions;

import com.batch.atm.operator.model.UserSession;
import com.batch.atm.operator.model.sm.ATMEvent;
import com.batch.atm.operator.model.sm.ATMState;
import com.batch.atm.operator.services.StateMachineService;
import com.batch.atm.operator.services.TransactionService;
import com.batch.atm.operator.services.impl.ATMStateMachineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.ReactiveAction;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class VerifyAccountBalanceAction implements ReactiveAction<ATMState, ATMEvent> {

    @Autowired
    TransactionService transactionService;

    @Autowired
    StateMachineService stateMachineService;

    @Override
    public Mono<Void> apply(StateContext<ATMState, ATMEvent> stateContext) {
        UserSession session = (UserSession) stateContext.getMessageHeader(
                ATMStateMachineService.USER_SESSION_HEADER
        );

        return Flux.fromStream(session.getTransactions().stream())
                .map(transaction -> transactionService.verifyAccountBalance(transaction))
                .collectList()
                .then()
                .and(stateMachineService.sendEvent(ATMEvent.CHECK_BALANCE, stateContext.getStateMachine()))
                .then();
    }
}
