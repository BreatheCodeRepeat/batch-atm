package com.batch.atm.operator.batch;

import com.batch.atm.operator.model.UserSession;
import com.batch.atm.operator.services.ATMTransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.file.transform.DefaultFieldSet;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component
@Slf4j
public class TransactionChunkProcessor implements ItemProcessor<UserSession,List<String>> {

    private ATMTransactionService transactionService;

    @Override
    public List<String> process(UserSession userSession) throws Exception {
        log.info("Started process");
        log.info(userSession.toString());
        List<String> results = new ArrayList<>();
        results.add("ATM_ERR");
        return results;
    }
}
