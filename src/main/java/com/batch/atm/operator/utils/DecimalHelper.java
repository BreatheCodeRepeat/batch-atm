package com.batch.atm.operator.utils;

import java.math.BigDecimal;

public class DecimalHelper {

    public static boolean isDecimalBiggerThanZero(BigDecimal decimal) {
        return decimal.compareTo(new BigDecimal(0)) > 0;
    }

    public static boolean isDecimalZero(BigDecimal decimal) {
        return decimal.equals(new BigDecimal(0));
    }

    public static boolean isDecimalSmallerThanZero(BigDecimal decimal) {
        return decimal.compareTo(new BigDecimal(0)) < 0;
    }

    public static BigDecimal copyDecimal(BigDecimal decimal) {
        return new BigDecimal(decimal.toBigInteger(), decimal.scale());
    }
}
