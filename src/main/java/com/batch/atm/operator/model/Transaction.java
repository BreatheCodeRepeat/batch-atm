package com.batch.atm.operator.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

public interface Transaction {
    String getSymbol();
}
