package com.batch.atm.operator.services.impl;

import com.batch.atm.operator.model.ErrorCode;
import com.batch.atm.operator.model.Transaction;
import com.batch.atm.operator.model.UserBalance;
import com.batch.atm.operator.model.UserCredentials;
import com.batch.atm.operator.model.UserSession;
import com.batch.atm.operator.model.WithdrawTransaction;
import com.batch.atm.operator.services.TransactionService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

import static com.batch.atm.operator.utils.DecimalHelper.copyDecimal;
import static com.batch.atm.operator.utils.DecimalHelper.isDecimalBiggerThanZero;
import static com.batch.atm.operator.utils.DecimalHelper.isDecimalSmallerThanZero;
import static com.batch.atm.operator.utils.DecimalHelper.isDecimalZero;

@Service
@RequiredArgsConstructor
@Slf4j
public class ATMTransactionService implements TransactionService {

    @Getter
    private BigDecimal amount;

    @Getter
    private UserSession userSession;

    @Override
    public synchronized void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @Override
    public synchronized void setUserSession(UserSession session) {
            this.userSession = session;
    }

    @Override
    public boolean verifyPin(UserCredentials credentials) {
        return credentials.getPin() == credentials.getInsertPin();
    }

    @Override
    public Transaction retrieveAmount(Transaction transaction,UserSession session){
        if(!transaction.hasError() && isDecimalBiggerThanZero(amount) || isDecimalZero(amount)){
            if(transaction instanceof WithdrawTransaction){
                BigDecimal retainAmount = copyDecimal(amount);
                retainAmount = retainAmount.subtract(((WithdrawTransaction) transaction).getAmount());
                if(isDecimalSmallerThanZero(retainAmount)){
                    revertTransaction(transaction,session);
                    transaction.setErrorCode(ErrorCode.ATM_ERR);
                    return transaction;
                }
                else{
                    setAmount(retainAmount);
                    return transaction;
                }
            }
        }
        return transaction;
    }

    @Override
    public void revertTransaction(Transaction transaction,UserSession session){
        session.setBalance(new UserBalance(
                session.getRevertingBalance().getAmount(),
                session.getRevertingBalance().getOverdraft()
        ));
    }

    @Override
    public Transaction verifyAccountBalance(Transaction transaction){
        if(!transaction.hasError()) {
            if (transaction instanceof WithdrawTransaction) {
                WithdrawTransaction withdrawTransaction = (WithdrawTransaction) transaction;
                UserBalance balance = userSession.getBalance();
                transaction.setBalance(copyDecimal(balance.getAmount()));
                BigDecimal totalAmountToWithdraw = balance.getAmount().add(balance.getOverdraft());
                if (totalAmountToWithdraw.compareTo((withdrawTransaction.getAmount())) < 0) {
                    withdrawTransaction.setErrorCode(ErrorCode.FUNDS_ERR);
                    return withdrawTransaction;
                } else if (balance.getAmount().compareTo(withdrawTransaction.getAmount()) <= 0) {
                    balance.setOverdraft(balance.getOverdraft().subtract(
                            withdrawTransaction
                                    .getAmount()
                                    .subtract(balance.getAmount())
                    ));
                    balance.setAmount(new BigDecimal(0));
                    transaction.setBalance(new BigDecimal(0));
                    return withdrawTransaction;
                } else {
                    balance.setAmount(balance.getAmount().subtract(withdrawTransaction.getAmount()));
                    transaction.setBalance(copyDecimal(balance.getAmount()));
                }
            } else {
                transaction.setBalance(copyDecimal(userSession.getBalance().getAmount()));
            }
        }
        return transaction;
    }
}
