package com.batch.atm.operator.model;

public enum TransactionType {
    BALANCE("B"),WITHDRAW("W");

    private String typeSymbol;

    TransactionType(String type){
        this.typeSymbol = type;
    }
}
