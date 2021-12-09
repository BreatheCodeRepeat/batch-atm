package com.batch.atm.operator.statemachine.actions;

import com.batch.atm.operator.model.UserBalance;
import com.batch.atm.operator.model.UserSession;
import com.batch.atm.operator.model.sm.ATMEvent;
import com.batch.atm.operator.model.sm.ATMState;
import com.batch.atm.operator.services.StateMachineService;
import com.batch.atm.operator.services.TransactionService;
import com.batch.atm.operator.services.impl.ATMStateMachineService;
import com.batch.atm.operator.utils.DecimalHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.ReactiveAction;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class AllocateFundsAction implements ReactiveAction<ATMState, ATMEvent> {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private StateMachineService stateMachineService;

    @Override
    public Mono<Void> apply(StateContext<ATMState, ATMEvent> stateContext) {
        UserSession session = (UserSession) stateContext.getMessageHeader(
                ATMStateMachineService.USER_SESSION_HEADER
        );
        UserBalance initialBalance = session.getBalance();
        UserBalance revertingBalance = new UserBalance(
                DecimalHelper.copyDecimal(initialBalance.getAmount()),
                DecimalHelper.copyDecimal(initialBalance.getOverdraft())
        );
        session.setRevertingBalance(revertingBalance);
        return processTransactions(session, stateContext);
    }

    private Mono<Void> processTransactions(UserSession session, StateContext<ATMState, ATMEvent> stateContext) {
        return Flux.fromStream(session.getTransactions().stream())
                .map(transaction -> transactionService.retrieveAmount(transaction))
                .collectList()
                .then()
                .and(stateMachineService.sendEvent(ATMEvent.ATM_HAS_CASH,stateContext.getStateMachine()))
                .then();
    }
}
