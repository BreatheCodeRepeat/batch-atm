package com.batch.atm.operator.model.sm;

public enum ATMState {
    BALANCE_OK,
    FUND_ERROR,
    INIT_SESSION,
    INIT_TRANSACTION,
    DECLINED_TRANSACTIONS,
    ATM_READY,
    ATM_EMPTY,
    DISPOSE_ATM
}
