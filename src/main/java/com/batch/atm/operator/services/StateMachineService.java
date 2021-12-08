package com.batch.atm.operator.services;

import com.batch.atm.operator.model.UserCredentials;
import com.batch.atm.operator.model.UserSession;
import com.batch.atm.operator.model.sm.ATMEvent;
import com.batch.atm.operator.model.sm.ATMState;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.StateMachineEventResult;
import reactor.core.publisher.Flux;

import java.math.BigDecimal;

public interface StateMachineService {
    Flux<StateMachineEventResult<ATMState, ATMEvent>> sendEvent(UserSession session, ATMEvent event, StateMachine<ATMState, ATMEvent> sm);
    Flux<StateMachineEventResult<ATMState, ATMEvent>> sendEvent(UserCredentials credentials, ATMEvent event, StateMachine<ATMState, ATMEvent> sm);
    Flux<StateMachineEventResult<ATMState, ATMEvent>> sendEvent(ATMEvent event, StateMachine<ATMState, ATMEvent> sm);
    Flux<StateMachineEventResult<ATMState, ATMEvent>> sendEvent(BigDecimal amount, UserSession session, ATMEvent event, StateMachine<ATMState,ATMEvent> sm);
}
