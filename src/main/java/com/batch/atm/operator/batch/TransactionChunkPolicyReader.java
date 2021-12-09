package com.batch.atm.operator.batch;

import com.batch.atm.operator.model.BalanceTransaction;
import com.batch.atm.operator.model.Transaction;
import com.batch.atm.operator.model.UserBalance;
import com.batch.atm.operator.model.UserCredentials;
import com.batch.atm.operator.model.UserSession;
import com.batch.atm.operator.model.WithdrawTransaction;
import com.batch.atm.operator.utils.Readers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.PassThroughFieldSetMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.batch.item.file.transform.FixedLengthTokenizer;
import org.springframework.batch.item.file.transform.LineTokenizer;
import org.springframework.batch.item.file.transform.PatternMatchingCompositeLineTokenizer;
import org.springframework.batch.item.file.transform.Range;
import org.springframework.batch.item.support.SingleItemPeekableItemReader;
import org.springframework.batch.repeat.RepeatContext;
import org.springframework.batch.repeat.context.RepeatContextSupport;
import org.springframework.batch.repeat.policy.CompletionPolicySupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class TransactionChunkPolicyReader extends CompletionPolicySupport implements ItemReader<UserSession> {

    private BlankLineCompletionContext cc;
    private SingleItemPeekableItemReader<FieldSet> peekAbleDelegate;

    @Override
    public boolean isComplete(RepeatContext context) {
        return this.cc.isComplete();
    }

    @Override
    public RepeatContext start(RepeatContext context) {
        this.cc = new BlankLineCompletionContext(context);
        return cc;
    }

    @Autowired
    private Resource inputResource;

    private static final String ACCOUNT_DETAILS = "*";
    private static final String WITHDRAW_TRANSACTION = "W*";
    private static final String BALANCE_TRANSACTION = "B";

    @Override
    public UserSession read() throws Exception {
        log.info("Start reading chunks from file");
        int linesCounter = 0;

        UserSession.UserSessionBuilder sessionBuilder = UserSession.builder();
        List<Transaction> transactions = new ArrayList<>();

        FieldSet line;
        String[] lineValues;
        for (linesCounter = 0; (line = peekAbleDelegate.read()) != null; linesCounter++) {
            lineValues = line.getValues();
            if (line.getFieldCount() == 3) {
                sessionBuilder.credentials(
                        new UserCredentials(
                                Long.parseLong(lineValues[0]),
                                Integer.parseInt(lineValues[1]),
                                Integer.parseInt(lineValues[2])
                        )
                );
            } else if (line.getFieldCount() == 2) {
                if (lineValues[0].equals("W")) {
                    transactions.add(
                            new WithdrawTransaction(
                                    Readers.parseDecimal(lineValues[1])
                            )
                    );
                } else {
                    sessionBuilder.balance(
                            new UserBalance(
                                    Readers.parseDecimal(lineValues[0]),
                                    Readers.parseDecimal(lineValues[1])
                            )
                    );
                }
            } else if (line.getFieldCount() == 1 && lineValues[0].equals("B")) {
                transactions.add(
                        new BalanceTransaction()
                );
            }

            FieldSet nextSet = peekAbleDelegate.peek();

            if (nextSet == null || nextSet.getFieldCount() == 0) {
                break;
            }
        }

        if (linesCounter == 0) {
            return null;
        }

        sessionBuilder.transactions(transactions);
        return sessionBuilder.build();
    }

    @BeforeStep
    public void beforeStep(StepExecution stepExecution) {
        FlatFileItemReader<FieldSet> delegate = new FlatFileItemReader<>();
        delegate.setResource(inputResource);
        delegate.setLinesToSkip(1);
        delegate.setLineMapper(buildLineMapper());
        delegate.open(stepExecution.getExecutionContext());
        peekAbleDelegate = new SingleItemPeekableItemReader<>();
        peekAbleDelegate.setDelegate(delegate);
    }

    @AfterStep
    public void afterStep(StepExecution stepExecution) {
        peekAbleDelegate.close();
    }

    private DefaultLineMapper<FieldSet> buildLineMapper() {
        final DefaultLineMapper<FieldSet> defaultLineMapper = new DefaultLineMapper<>();
        final PatternMatchingCompositeLineTokenizer compositeLineTokenizer = new PatternMatchingCompositeLineTokenizer();
        final Map<String, LineTokenizer> tokenizers = new HashMap<>();

        tokenizers.put(ACCOUNT_DETAILS, buildSpaceDelimitedTokenizer());
        tokenizers.put(WITHDRAW_TRANSACTION, buildSpaceDelimitedTokenizer());
        tokenizers.put(BALANCE_TRANSACTION, buildBalanceTransactionTokenizer());

        compositeLineTokenizer.setTokenizers(tokenizers);
        defaultLineMapper.setLineTokenizer(compositeLineTokenizer);
        defaultLineMapper.setFieldSetMapper(new PassThroughFieldSetMapper());

        return defaultLineMapper;
    }

    private LineTokenizer buildBalanceTransactionTokenizer() {
        FixedLengthTokenizer tokenizer = new FixedLengthTokenizer();

        tokenizer.setColumns(
                new Range(1, 1)
        );
        tokenizer.setNames("B");
        tokenizer.setStrict(false);
        return tokenizer;
    }

    private LineTokenizer buildSpaceDelimitedTokenizer() {
        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer(" ");
        tokenizer.setStrict(false);
        return tokenizer;
    }

    protected class BlankLineCompletionContext extends RepeatContextSupport {

        public BlankLineCompletionContext(RepeatContext context) {
            super(context);
        }

        public boolean isComplete() {
            final FieldSet next;
            try {
                next = peekAbleDelegate.peek();
            } catch (Exception e) {
                throw new NonTransientResourceException("Unable to peek", e);
            }
            return next == null || next.getFieldCount() == 0;
        }
    }

}
