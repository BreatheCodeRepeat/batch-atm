package com.batch.atm.operator.utils;

import com.batch.atm.operator.exceptions.MalformedATMAmountException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;

public class Readers {
    public static BigDecimal readAtmAmount(String path) throws MalformedATMAmountException {
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            return parseDecimal(br.readLine());
        }
        catch (IOException e){
            throw new MalformedATMAmountException(
                    String.format("Could read text file located at %s",path),e
            );
        }
        catch (ParseException e){
            throw new MalformedATMAmountException(
                    String.format("Could not parse ATM amount decimal provided in text file located at %s",path),e
            );
        }
    }

    public static BigDecimal parseDecimal(String decimal) throws ParseException {
        String pattern = "#,##0.0#";

        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator(',');
        symbols.setDecimalSeparator('.');

        DecimalFormat decimalFormat = new DecimalFormat(pattern, symbols);
        decimalFormat.setParseBigDecimal(true);

        return (BigDecimal) decimalFormat.parse(decimal);
    }
}
