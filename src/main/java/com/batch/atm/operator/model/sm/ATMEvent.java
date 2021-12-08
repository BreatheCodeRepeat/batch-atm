package com.batch.atm.operator.model.sm;

public enum ATMEvent {
    READ_SESSION,
    AUTH_EVENT,
    AUTH_APPROVE,
    AUTH_DECLINE,
    BATCH_COMPLETE,
    PROCESS_TRANSACTION,
    NOT_ENOUGHT_BALANCE,
    CHECK_BALANCE,
    NEW_TRANSACTION,
    ACCEPT_TRANSACTION,
    NOT_ENOUGHT_CASH,
    ATM_HAS_CASH
}
