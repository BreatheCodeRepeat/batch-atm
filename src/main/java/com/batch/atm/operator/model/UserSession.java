package com.batch.atm.operator.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UserSession {
    private UserCredentials credentials;
    private UserBalance balance;
    private List<Transaction> transactions;
}
