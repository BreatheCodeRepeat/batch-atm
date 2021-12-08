package com.batch.atm.operator.config;

import com.batch.atm.operator.model.sm.ATMEvent;
import com.batch.atm.operator.model.sm.ATMState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.action.ReactiveAction;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;

import java.util.EnumSet;
import java.util.Optional;

@Slf4j
@Configuration
@EnableStateMachine
public class StateMachineConfig extends
        EnumStateMachineConfigurerAdapter<ATMState, ATMEvent> {

    @Autowired
    private ReactiveAction<ATMState, ATMEvent> authAction;

    @Autowired
    private ReactiveAction<ATMState, ATMEvent> verifyAccountBalanceAction;

    @Autowired
    private ReactiveAction<ATMState, ATMEvent> allocateFundsAction;

    @Override
    public void configure(StateMachineStateConfigurer<ATMState, ATMEvent> states) throws Exception {
        states.withStates()
                .initial(ATMState.INIT_SESSION)
                .states(EnumSet.allOf(ATMState.class))
                .end(ATMState.DISPOSE_ATM);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<ATMState, ATMEvent> transitions) throws Exception {
        setUpSessionStates(transitions);

        setState(transitions, ATMState.INIT_TRANSACTION, ATMEvent.PROCESS_TRANSACTION, allocateFundsAction);

        setState(transitions, ATMState.INIT_TRANSACTION, ATMState.ATM_READY, ATMEvent.ATM_HAS_CASH);
        setState(transitions, ATMState.INIT_TRANSACTION, ATMState.ATM_EMPTY, ATMEvent.NOT_ENOUGHT_CASH);

        setState(transitions, ATMState.ATM_READY, ATMEvent.ACCEPT_TRANSACTION, verifyAccountBalanceAction);

        setState(transitions, ATMState.ATM_READY, ATMState.BALANCE_OK, ATMEvent.CHECK_BALANCE);
        setState(transitions, ATMState.ATM_READY, ATMState.FUND_ERROR, ATMEvent.NOT_ENOUGHT_BALANCE);

        setState(transitions, ATMState.BALANCE_OK, ATMState.INIT_TRANSACTION, ATMEvent.NEW_TRANSACTION);
        setState(transitions, ATMState.ATM_EMPTY, ATMState.INIT_TRANSACTION, ATMEvent.NEW_TRANSACTION);
        setState(transitions, ATMState.FUND_ERROR, ATMState.INIT_TRANSACTION, ATMEvent.NEW_TRANSACTION);

        setState(transitions, ATMState.INIT_TRANSACTION, ATMState.INIT_SESSION, ATMEvent.READ_SESSION);

    }

    private void setUpSessionStates(StateMachineTransitionConfigurer<ATMState, ATMEvent> transitions) throws Exception {
        setState(transitions, ATMState.INIT_SESSION, ATMState.INIT_SESSION, ATMEvent.READ_SESSION);
        setState(transitions, ATMState.INIT_SESSION, ATMEvent.AUTH_EVENT, authAction);
        setState(transitions, ATMState.INIT_SESSION, ATMState.INIT_TRANSACTION, ATMEvent.AUTH_APPROVE);
        setState(transitions, ATMState.INIT_SESSION, ATMState.DECLINED_TRANSACTIONS, ATMEvent.AUTH_DECLINE);
        setState(transitions, ATMState.DECLINED_TRANSACTIONS, ATMState.INIT_SESSION, ATMEvent.READ_SESSION);
        setState(transitions, ATMState.INIT_SESSION, ATMState.DISPOSE_ATM, ATMEvent.BATCH_COMPLETE);
    }

    @Override
    public void configure(StateMachineConfigurationConfigurer<ATMState, ATMEvent> config) throws Exception {
        StateMachineListenerAdapter<ATMState, ATMEvent> adapter = new StateMachineListenerAdapter<ATMState, ATMEvent>() {
            @Override
            public void stateChanged(State from, State to) {
                log.info(String.format("state change from %s , %s",
                        (ATMState) Optional.ofNullable(from).map(State::getId).orElse(ATMState.INIT_SESSION),
                        (ATMState) Optional.ofNullable(to).map(State::getId).orElse(ATMState.INIT_SESSION)));
            }
        };
        config.withConfiguration().autoStartup(true).listener(adapter);
    }

    private void setState(
            StateMachineTransitionConfigurer<ATMState, ATMEvent> configurer,
            ATMState sourceState,
            ATMState targetState,
            ATMEvent event
    ) throws Exception {
        configurer.withExternal()
                .source(sourceState)
                .target(targetState)
                .event(event)
                .and();
    }

    private void setState(
            StateMachineTransitionConfigurer<ATMState, ATMEvent> configurer,
            ATMState sourceState,
            ATMEvent event,
            Action<ATMState, ATMEvent> action
    ) throws Exception {
        configurer.withExternal()
                .source(sourceState)
                .target(sourceState)
                .event(event)
                .action(action)
                .and();
    }

    private void setState(
            StateMachineTransitionConfigurer<ATMState, ATMEvent> configurer,
            ATMState sourceState,
            ATMEvent event,
            ReactiveAction<ATMState, ATMEvent> action
    ) throws Exception {
        configurer.withExternal()
                .source(sourceState)
                .target(sourceState)
                .event(event)
                .actionFunction(action)
                .and();
    }
}
