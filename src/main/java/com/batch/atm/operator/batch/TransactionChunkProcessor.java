package com.batch.atm.operator.batch;

import com.batch.atm.operator.model.Transaction;
import com.batch.atm.operator.model.UserBalance;
import com.batch.atm.operator.model.UserSession;
import com.batch.atm.operator.services.impl.ATMTransactionService;
import com.batch.atm.operator.services.ProcessingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import static com.batch.atm.operator.utils.DecimalHelper.copyDecimal;


@Component
@Slf4j
public class TransactionChunkProcessor implements ItemProcessor<UserSession, List<String>> {

    @Autowired
    private ProcessingService processingService;

    @Autowired
    ATMTransactionService atmTransactionService;

    @Override
    public List<String> process(UserSession userSession) throws Exception {
        Long iban = userSession.getCredentials().getIban();
        log.info("Started process");
        log.info("Processing account transactions {}", iban);
        atmTransactionService.setUserSession(userSession);
        setTransactionsBalance(userSession);
        processingService.processSession(userSession).blockLast();
        return generateOutputAsString(userSession);
    }

    private void setTransactionsBalance(UserSession session) {
        UserBalance balance = session.getBalance();
        session.getTransactions()
                .forEach(
                        transaction -> transaction.setBalance(copyDecimal(balance.getAmount()))
                );
    }

    private List<String> generateOutputAsString(UserSession session) {
        return session.getTransactions()
                .stream()
                .map(this::outputTransaction)
                .collect(Collectors.toList());
    }

    private String outputTransaction(Transaction transaction) {
        if (transaction.hasError()) {
            return transaction.getErrorCode().toString();
        }
        return transaction.getBalance().toString();
    }

}
