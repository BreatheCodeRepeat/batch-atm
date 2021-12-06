package com.batch.atm.operator.config;

import com.batch.atm.operator.model.sm.ATMEvent;
import com.batch.atm.operator.model.sm.ATMState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;

import java.util.EnumSet;

@Slf4j
@Configuration
@EnableStateMachineFactory
public class StateMachineConfig extends
        StateMachineConfigurerAdapter<ATMState, ATMEvent> {
    @Override
    public void configure(StateMachineStateConfigurer<ATMState, ATMEvent> states) throws Exception {
        states.withStates()
                .initial(ATMState.INIT_ATM)
                .states(EnumSet.allOf(ATMState.class))
                .end(ATMState.DISPOSE_ATM);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<ATMState, ATMEvent> transitions) throws Exception {
        transitions = setState(transitions,ATMState.INIT_ATM,ATMState.INIT_SESSION,ATMEvent.READ_SESSION);
        transitions = setState(transitions,ATMState.INIT_ATM,ATMState.INIT_SESSION,ATMEvent.READ_SESSION);
        transitions = setState(transitions,ATMState.INIT_ATM,ATMState.INIT_SESSION,ATMEvent.READ_SESSION);
        transitions = setState(transitions,ATMState.INIT_ATM,ATMState.INIT_SESSION,ATMEvent.READ_SESSION);
        transitions = setState(transitions,ATMState.INIT_ATM,ATMState.INIT_SESSION,ATMEvent.READ_SESSION);
        transitions = setState(transitions,ATMState.INIT_ATM,ATMState.INIT_SESSION,ATMEvent.READ_SESSION);

    }

    @Override
    public void configure(StateMachineConfigurationConfigurer<ATMState, ATMEvent> config) throws Exception {
        StateMachineListenerAdapter<ATMState, ATMEvent> adapter = new StateMachineListenerAdapter<ATMState, ATMEvent>() {
            @Override
            public void stateChanged(State from, State to) {
                log.info(String.format("state change form %s , %s", from, to));
            }
        };
        config.withConfiguration().listener(adapter);
    }



    private StateMachineTransitionConfigurer<ATMState, ATMEvent> setState(
            StateMachineTransitionConfigurer<ATMState, ATMEvent> configurer,
            ATMState sourceState,
            ATMState targetState,
            ATMEvent event
    ) throws Exception {
        return configurer.withExternal()
                .source(sourceState)
                .target(targetState)
                .event(event)
                .and();
    }

    private StateMachineTransitionConfigurer<ATMState, ATMEvent> setState(
            StateMachineTransitionConfigurer<ATMState, ATMEvent> configurer,
            ATMState sourceState,
            ATMState targetState,
            ATMEvent event,
            Action<ATMState,ATMEvent> action
    ) throws Exception {
        return configurer.withExternal()
                .source(sourceState)
                .target(targetState)
                .event(event)
                .action(action)
                .and();
    }
}
