package com.batch.atm.operator.config;

import com.batch.atm.operator.model.UserSession;
import com.batch.atm.operator.batch.TransactionChunkPolicyReader;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableBatchProcessing
public class BatchJobConfig {

    @Bean
    public Job job(JobBuilderFactory jobBuilderFactory,
                   Step readTransactionsStep,
                   Step readAmountStep){
       return jobBuilderFactory.get("Transaction-Operator")
                .incrementer(new RunIdIncrementer())
                .start(readAmountStep)
                .next(readTransactionsStep)
                .build();
    }

    @Bean
    protected Step readTransactionsStep(TransactionChunkPolicyReader reader,
                                        ItemProcessor<UserSession, UserSession> processor,
                                        ItemWriter<UserSession> writer,
                                        StepBuilderFactory stepBuilderFactory) {

        return stepBuilderFactory.get("processTransactions")
                .<UserSession,UserSession>chunk(reader)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    @Bean
    protected Step readAmountStep(StepBuilderFactory stepBuilderFactory, Tasklet tasklet) {
        return stepBuilderFactory.get("readAmount")
                .tasklet(tasklet)
                .build();
    }

}
