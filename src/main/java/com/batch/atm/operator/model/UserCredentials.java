package com.batch.atm.operator.model;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserCredentials {
    private Long iban;
    private int pin;
    private int insertPin;
}
