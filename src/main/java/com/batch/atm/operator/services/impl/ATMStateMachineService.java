package com.batch.atm.operator.services.impl;

import com.batch.atm.operator.model.UserCredentials;
import com.batch.atm.operator.model.UserSession;
import com.batch.atm.operator.model.sm.ATMEvent;
import com.batch.atm.operator.model.sm.ATMState;
import com.batch.atm.operator.services.StateMachineService;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.StateMachineEventResult;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Service
public class ATMStateMachineService implements StateMachineService {
    public static final String USER_SESSION_HEADER = "userSession";
    public static final String USER_CREDENTIALS_HEADER = "userCredentials";
    public static final String AMOUNT_HEADER = "amount";

    @Override
    public Flux<StateMachineEventResult<ATMState, ATMEvent>> sendEvent(UserSession session, ATMEvent event, StateMachine<ATMState,ATMEvent> sm){
        return sm.sendEvent(
                Mono.fromSupplier(
                        () -> MessageBuilder.withPayload(event).setHeader(USER_SESSION_HEADER,session).build()
                ));
    }

    @Override
    public Flux<StateMachineEventResult<ATMState, ATMEvent>> sendEvent(BigDecimal amount, UserSession session, ATMEvent event, StateMachine<ATMState,ATMEvent> sm){
        return sm.sendEvent(
                Mono.fromSupplier(
                        () -> MessageBuilder.withPayload(event)
                                .setHeader(USER_SESSION_HEADER,session)
                                .setHeader(AMOUNT_HEADER,amount)
                                .build()
                ));
    }

    @Override
    public Flux<StateMachineEventResult<ATMState, ATMEvent>> sendEvent(UserCredentials credentials, ATMEvent event, StateMachine<ATMState, ATMEvent> sm) {
        return sm.sendEvent(
                Mono.fromSupplier(
                        () -> MessageBuilder.withPayload(event).setHeader(USER_CREDENTIALS_HEADER,credentials).build()
                ));
    }

    @Override
    public Flux<StateMachineEventResult<ATMState, ATMEvent>> sendEvent(ATMEvent event, StateMachine<ATMState,ATMEvent> sm){
        return sm.sendEvent(
                Mono.fromSupplier(
                        () -> MessageBuilder.withPayload(event).build()
                ));
    }
}
