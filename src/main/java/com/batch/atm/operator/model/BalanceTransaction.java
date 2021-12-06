package com.batch.atm.operator.model;

import lombok.Data;
import lombok.Getter;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.math.BigDecimal;

@Getter
public class BalanceTransaction implements Transaction {
    private final String symbol = "B";
}
