package com.batch.atm.operator.batch;

import com.batch.atm.operator.config.AppConfig;
import com.batch.atm.operator.model.*;
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
import org.springframework.batch.item.file.transform.*;
import org.springframework.batch.item.support.SingleItemPeekableItemReader;
import org.springframework.batch.repeat.RepeatContext;
import org.springframework.batch.repeat.context.RepeatContextSupport;
import org.springframework.batch.repeat.policy.CompletionPolicySupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Slf4j
public class TransactionChunkReader extends CompletionPolicySupport implements ItemReader<UserSession> {

//    private FlatFileItemReader<FieldSet> delegate;
    private TransactionChunkReader.EOFCompletionContext cc;
    private SingleItemPeekableItemReader<FieldSet> peekAbleDelegate;


    protected class EOFCompletionContext extends RepeatContextSupport
    {
        boolean eof = false;
        public EOFCompletionContext (RepeatContext context)
        {
            super(context);
        }

        public void update()
        {
            final FieldSet next;
            try
            {
                next = peekAbleDelegate.peek();
            }
            catch (Exception e)
            {
                throw new NonTransientResourceException("Unable to peek", e);
            }
            // EOF?
            this.eof = next.getFieldCount() == 0;
        }

        public boolean isComplete() {
            return this.eof;
        }
    }

    @Override
    public void update(RepeatContext context){
        this.cc.update();
    }

    @Override
    public boolean isComplete(RepeatContext context)
    {
        return this.cc.isComplete();
    }

    @Override
    public RepeatContext start(RepeatContext context)
    {
        this.cc = new EOFCompletionContext(context);
        return cc;
    }

    @Autowired
    private AppConfig config;

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
        for (linesCounter = 0 ;(line = peekAbleDelegate.read()) != null;linesCounter++) {
            lineValues = line.getValues();
            if(line.getFieldCount() == 3){
                sessionBuilder.credentials(
                        new UserCredentials(
                                Integer.parseInt(lineValues[0]),
                                Integer.parseInt(lineValues[1]),
                                Integer.parseInt(lineValues[2])
                        )
                );
            }
            else if(line.getFieldCount() == 2){
                if(lineValues[0].equals("W")){
                    transactions.add(
                            new WithdrawTransaction(
                                    Readers.parseDecimal(lineValues[1])
                            )
                    );
                }
                else {
                    sessionBuilder.balance(
                            new UserBalance(
                                    Readers.parseDecimal(lineValues[0]),
                                    Readers.parseDecimal(lineValues[1])
                            )
                    );
                }
            }
            else if(line.getFieldCount() == 1 && lineValues[0].equals("B")){
                    transactions.add(
                            new BalanceTransaction()
                    );
            }
//            if(line.getFieldCount() == 2 && line.getNames() == 0)

//            String[] fieldSetNames = line.getNames();
//            if(Arrays.equals(fieldSetNames, userCredentials)){
//
//            }
//            else if((Arrays.equals(fieldSetNames, userBalance))){
//
//            }
//            else if((Arrays.equals(fieldSetNames, withdrawTransactionFields))){
//
//            }
//            else if(fieldSetNames.length == 1 && fieldSetNames[0].equals("symbol")){
//
//            }
            if(peekAbleDelegate.peek().getFieldCount() == 0){
                break;
            }
        }

        if(linesCounter == 0){
            return null;
        }

        sessionBuilder.transactions(transactions);
        return sessionBuilder.build();
    }

    @BeforeStep
    public void beforeStep(StepExecution stepExecution) {
        FlatFileItemReader<FieldSet> delegate = new FlatFileItemReader<>();
        delegate.setResource(new ClassPathResource(config.getFileName()));
        delegate.setLinesToSkip(1);
//        delegate.setRecordSeparatorPolicy(new BlankLineRecordSeparatorPolicy());
        delegate.setLineMapper(buildLineMapper());
        delegate.open(stepExecution.getExecutionContext());
        peekAbleDelegate = new SingleItemPeekableItemReader<>();
        peekAbleDelegate.setDelegate(delegate);
    }

    @AfterStep
    public void afterStep(StepExecution stepExecution) {
        peekAbleDelegate.close();
    }

    private DefaultLineMapper<FieldSet> buildLineMapper(){
        final DefaultLineMapper<FieldSet> defaultLineMapper = new DefaultLineMapper<>();
        final PatternMatchingCompositeLineTokenizer compositeLineTokenizer = new PatternMatchingCompositeLineTokenizer();
        final Map<String, LineTokenizer> tokenizers = new HashMap<>();

        tokenizers.put(ACCOUNT_DETAILS, buildSpaceDelimitedTokenizer());
        tokenizers.put(WITHDRAW_TRANSACTION, buildSpaceDelimitedTokenizer());
        tokenizers.put(BALANCE_TRANSACTION, buildBalanceTransactionTokenizer());
//        tokenizers.put("*", buildAccountDetailsTokenizer());
//        tokenizers.put("W*", buildAccountDetailsTokenizer(1 ,3));
//        tokenizers.put("B", buildAccountDetailsTokenizer(1 ,3));

        compositeLineTokenizer.setTokenizers(tokenizers);
        defaultLineMapper.setLineTokenizer(compositeLineTokenizer);
        defaultLineMapper.setFieldSetMapper(new PassThroughFieldSetMapper());

        return defaultLineMapper;
    }

//    private LineTokenizer buildBalanceTransactionsTokenizer() {
//        FixedLengthTokenizer tokenizer = new FixedLengthTokenizer();
//
//        tokenizer.setColumns(
//                new Range(1, 1)
//        );
//
//        tokenizer.setNames("symbol");
//        tokenizer.setStrict(false);
//        return tokenizer;
//    }

//    private LineTokenizer buildAccountDetailsTokenizer() {
//        FixedLengthTokenizer tokenizer = new FixedLengthTokenizer();
//
//        tokenizer.setColumns(
//                new Range(1, 8),
//                new Range(10, 13),
//                new Range(15, 18)
//        );
//
//        tokenizer.setNames(userCredentials);
//        tokenizer.setStrict(false);
//        return tokenizer;
//    }

//    private LineTokenizer buildAccountDetailsTokenizer() {
//        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer(" ");
//        lineTokenizer.setStrict(false);
//        return lineTokenizer;
//    }

//    private LineTokenizer buildAccountDetailsTokenizer(int s, int f) {
//        FixedLengthTokenizer tokenizer = new FixedLengthTokenizer();
//        DelimitedLineTokenizer delimitedLineTokenizer = new DelimitedLineTokenizer(" ");
//        delimitedLineTokenizer.setNames("test","test2");
//        delimitedLineTokenizer.setStrict(false);
//        tokenizer.setColumns(
//                new Range(s, f)
//        );
//
//        tokenizer.setNames("test");
//        tokenizer.setStrict(false);
//        tokenizer.tokenize("1");
//        return tokenizer;
//    }

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
}
