package com.batch.atm.operator.model.sm;

public enum ATMState {
    INIT_ATM,
    INIT_SESSION,
    PROCESSING_TRANSACTION,
    CLOSE_SESSION,
    SESSION_AUTH,
    DISPOSE_ATM

}
