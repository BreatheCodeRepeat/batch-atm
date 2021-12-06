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
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.transform.PassThroughFieldExtractor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.util.List;

@Configuration
@EnableBatchProcessing
public class BatchJobConfig {

    @Autowired
    private AppConfig config;


    @Bean
    public Resource outputResource(){
        return new FileSystemResource(config.getTargetFileName());
    }

    @Bean
    public Resource inputResource(){
        return new FileSystemResource(config.getFileName());
    }

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
                                        ItemProcessor<UserSession, List<String>> processor,
                                        ItemWriter<List<String>> writer,
                                        StepBuilderFactory stepBuilderFactory) {

        return stepBuilderFactory.get("processTransactions")
                .<UserSession,List<String>>chunk(reader)
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

    @Bean
    protected ItemWriter<List<String>> itemWriter(Resource outputResource) {
        FlatFileItemWriterBuilder<List<String>> builder = new FlatFileItemWriterBuilder<>();
        builder.name("itemWriter");
        builder.resource(outputResource);
        builder.shouldDeleteIfExists(true);
        builder.transactional(true);
        builder.delimited().fieldExtractor(new PassThroughFieldExtractor<>());
        return builder.build();
    }

}
