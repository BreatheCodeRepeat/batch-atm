package com.batch.atm.operator.model;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserCredentials {
    private int iban;
    private int pin;
    private int insertPin;
}
