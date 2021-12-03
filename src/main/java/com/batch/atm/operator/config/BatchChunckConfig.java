package com.batch.atm.operator.config;

import com.batch.atm.operator.model.Transaction;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableBatchProcessing
public class BatchChunckConfig {

    @Bean
    public Job job(JobBuilderFactory jobBuilderFactory,
                   ItemReader<Transaction> itemReader,
                   ItemProcessor<Transaction, Transaction> itemProcessor,
                   ItemWriter<Transaction> itemWriter,
                   Step readTransactionsStep,
                   Step readAmountStep){
       return jobBuilderFactory.get("Transaction-Operator")
                .incrementer(new RunIdIncrementer())
                .start(readAmountStep)
                .next(readTransactionsStep)
                .build();
    }

    @Bean
    public ItemReader<Transaction> itemReader() {
        return null;
    }

    @Bean
    public ItemProcessor<Transaction, Transaction> itemProcessor() {
        return null;
    }

    @Bean
    public ItemWriter<Transaction> itemWriter() {
        return null;
    }

    @Bean
    protected Step readTransactionsStep(ItemReader<Transaction> reader,
                                ItemProcessor<Transaction, Transaction> processor,
                                ItemWriter<Transaction> writer,
                                StepBuilderFactory stepBuilderFactory) {
        return stepBuilderFactory.get("processTransactions")
                .<Transaction, Transaction> chunk(2)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    @Bean
    protected Step readAmountStep(StepBuilderFactory stepBuilderFactory) {
        return stepBuilderFactory.get("readAmount")
                .tasklet(null)
                .build();
    }

}
