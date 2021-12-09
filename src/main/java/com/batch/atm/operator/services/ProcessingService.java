package com.batch.atm.operator.services;

import com.batch.atm.operator.model.sm.ATMEvent;
import com.batch.atm.operator.model.sm.ATMState;
import org.springframework.statemachine.StateMachineEventResult;
import reactor.core.publisher.Flux;

public interface ProcessingService {
    Flux<StateMachineEventResult<ATMState, ATMEvent>> processSession();

}
