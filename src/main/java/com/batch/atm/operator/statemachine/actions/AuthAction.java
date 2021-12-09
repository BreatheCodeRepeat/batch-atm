package com.batch.atm.operator.statemachine.actions;

import com.batch.atm.operator.model.ErrorCode;
import com.batch.atm.operator.model.UserSession;
import com.batch.atm.operator.model.sm.ATMEvent;
import com.batch.atm.operator.model.sm.ATMState;
import com.batch.atm.operator.services.impl.ATMStateMachineService;
import com.batch.atm.operator.services.StateMachineService;
import com.batch.atm.operator.services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.ReactiveAction;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class AuthAction implements ReactiveAction<ATMState, ATMEvent> {

    @Autowired
    private StateMachineService stateMachineService;

    @Autowired
    private TransactionService transactionService;

    @Override
    public Mono<Void> apply(StateContext<ATMState, ATMEvent> stateContext) {
        UserSession session = (UserSession) stateContext.getMessageHeader(
                ATMStateMachineService.USER_SESSION_HEADER
        );
        if (transactionService.verifyPin(session.getCredentials())) {
            return stateMachineService.sendEvent(
                            session, ATMEvent.AUTH_APPROVE,
                            stateContext.getStateMachine()
                    ).collectList()
                    .then();
        } else {
            return stateMachineService.sendEvent(
                            session, ATMEvent.AUTH_DECLINE,
                            stateContext.getStateMachine()
                    ).flatMap(p -> Flux.fromStream(session.getTransactions().stream()))
                    .map(transaction -> {
                        transaction.setErrorCode(ErrorCode.ACCOUNT_ERR);
                        return transaction;
                    }).collectList()
                    .then();
        }
    }
}
