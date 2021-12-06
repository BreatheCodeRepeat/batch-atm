package com.batch.atm.operator.utils;

import com.batch.atm.operator.exceptions.MalformedAmountException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;

public class Readers {
    public static BigDecimal readAtmAmount(Resource resource) throws MalformedAmountException {
        try (BufferedReader br = new BufferedReader(new FileReader(resource.getFile()))) {
            return parseDecimal(br.readLine());
        }
        catch (IOException e){
            throw new MalformedAmountException(
                    String.format("Could read text file located at %s",resource.getFilename()),e
            );
        }
        catch (ParseException e){
            throw new MalformedAmountException(
                    String.format("Could not parse ATM amount decimal provided in text file located at %s",resource.getFilename()),e
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
