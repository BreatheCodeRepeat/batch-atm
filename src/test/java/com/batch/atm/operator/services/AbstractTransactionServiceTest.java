package com.batch.atm.operator.services;

import com.batch.atm.operator.model.BalanceTransaction;
import com.batch.atm.operator.model.Transaction;
import com.batch.atm.operator.model.UserBalance;
import com.batch.atm.operator.model.UserCredentials;
import com.batch.atm.operator.model.UserSession;
import com.batch.atm.operator.model.WithdrawTransaction;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractTransactionServiceTest {

    protected UserSession createWrongPINMockSession(){
        UserSession.UserSessionBuilder sessionBuilder = UserSession.builder();
        BalanceTransaction balanceTransaction = new BalanceTransaction();
        WithdrawTransaction withdrawTransaction = new WithdrawTransaction(new BigDecimal(100));
        List<Transaction> transactionList = new ArrayList<Transaction>();
        transactionList.add(balanceTransaction);
        transactionList.add(withdrawTransaction);
        UserCredentials credentials = new UserCredentials(12345678L ,1234, 4321);
        UserBalance balance = new UserBalance(new BigDecimal(500),new BigDecimal(100));
        return sessionBuilder.transactions(transactionList).balance(balance).credentials(credentials).build();
    }

    protected UserSession createATMRetrievalRollBackSession(){
        UserSession.UserSessionBuilder sessionBuilder = UserSession.builder();
        WithdrawTransaction withdrawTransaction = new WithdrawTransaction(new BigDecimal(2000));
        List<Transaction> transactionList = new ArrayList<Transaction>();
        transactionList.add(withdrawTransaction);
        UserCredentials credentials = new UserCredentials(12345678L ,1234, 1234);
        UserBalance balance = new UserBalance(new BigDecimal(3000),new BigDecimal(100));
        return sessionBuilder.transactions(transactionList).balance(balance).credentials(credentials).build();
    }

    protected UserSession createSucessTransactionsSession(){
        UserSession.UserSessionBuilder sessionBuilder = UserSession.builder();
        WithdrawTransaction withdrawTransaction = new WithdrawTransaction(new BigDecimal(2000));
        List<Transaction> transactionList = new ArrayList<Transaction>();
        transactionList.add(withdrawTransaction);
        UserCredentials credentials = new UserCredentials(12345678L ,1234, 1234);
        UserBalance balance = new UserBalance(new BigDecimal(3000),new BigDecimal(100));
        return sessionBuilder.transactions(transactionList).balance(balance).credentials(credentials).build();
    }

    protected UserSession createRoolbackTransactionsSession(){
        UserSession.UserSessionBuilder sessionBuilder = UserSession.builder();
        WithdrawTransaction withdrawTransaction = new WithdrawTransaction(new BigDecimal(2000));
        List<Transaction> transactionList = new ArrayList<Transaction>();
        transactionList.add(withdrawTransaction);
        UserCredentials credentials = new UserCredentials(12345678L ,1234, 1234);
        UserBalance balance = new UserBalance(new BigDecimal(1000),new BigDecimal(100));
        return sessionBuilder.transactions(transactionList).balance(balance).credentials(credentials).build();
    }

    protected UserSession createZeroBalanceOverdraftTransactionsSession(){
        UserSession.UserSessionBuilder sessionBuilder = UserSession.builder();
        WithdrawTransaction withdrawTransaction = new WithdrawTransaction(new BigDecimal(1100));
        List<Transaction> transactionList = new ArrayList<Transaction>();
        transactionList.add(withdrawTransaction);
        UserCredentials credentials = new UserCredentials(12345678L ,1234, 1234);
        UserBalance balance = new UserBalance(new BigDecimal(1000),new BigDecimal(500));
        return sessionBuilder.transactions(transactionList).balance(balance).credentials(credentials).build();
    }

    protected boolean checkAllTransactionsHaveErrors(UserSession session){
        return  session
                .getTransactions()
                .stream()
                .map(Transaction::hasError)
                .reduce((transaction, transaction2) -> transaction && transaction2)
                .get();
    }

    protected boolean checkSomeTransactionsHaveErrors(UserSession session){
        return  session
                .getTransactions()
                .stream()
                .map(Transaction::hasError)
                .reduce((transaction, transaction2) -> transaction || transaction2)
                .get();
    }
}
