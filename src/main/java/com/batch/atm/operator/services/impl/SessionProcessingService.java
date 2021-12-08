package com.batch.atm.operator.services.impl;

import com.batch.atm.operator.model.UserSession;
import com.batch.atm.operator.model.sm.ATMEvent;
import com.batch.atm.operator.model.sm.ATMState;
import com.batch.atm.operator.services.ProcessingService;
import com.batch.atm.operator.services.StateMachineService;
import lombok.Getter;
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

    @Getter
    private UserSession userSession;

    public SessionProcessingService() {
    }

    @Override
    public Flux<StateMachineEventResult<ATMState, ATMEvent>> processSession(UserSession session) {
        this.userSession = session;
        return stateMachineService.sendEvent(ATMEvent.READ_SESSION, stateMachine)
                .flatMap(result -> stateMachineService.sendEvent(session, ATMEvent.AUTH_EVENT, stateMachine))
                .flatMap(this::processATMRetrieval)
                .flatMap(result -> stateMachineService.sendEvent(userSession, ATMEvent.ACCEPT_TRANSACTION, stateMachine))
                .flatMap(result -> stateMachineService.sendEvent(userSession, ATMEvent.NEW_TRANSACTION, stateMachine));
    }

    private Flux<StateMachineEventResult<ATMState, ATMEvent>> processATMRetrieval(StateMachineEventResult<ATMState, ATMEvent> result) {
        ATMState state = stateMachine.getState().getId();
        if (state.equals(ATMState.INIT_TRANSACTION)) {
            return stateMachineService.sendEvent(userSession, ATMEvent.PROCESS_TRANSACTION, stateMachine);
        } else if (state.equals(ATMState.DECLINED_TRANSACTIONS)) {
            return stateMachineService.sendEvent(ATMEvent.READ_SESSION, stateMachine);
        }
        return Flux.empty();
    }


}
