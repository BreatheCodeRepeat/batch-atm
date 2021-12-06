package com.batch.atm.operator.batch;

import com.batch.atm.operator.config.AppConfig;
import com.batch.atm.operator.services.TransactionService;
import com.batch.atm.operator.utils.Readers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Slf4j
@Component
public class AmountReaderTasklet implements Tasklet {

    @Autowired
    private AppConfig appConfig;
    @Autowired
    private TransactionService transactionService;

    @Override
    public RepeatStatus execute(StepContribution stepContribution,
                                ChunkContext chunkContext) throws Exception {
        Resource resource = new FileSystemResource(appConfig.getFileName());
        BigDecimal amount = Readers.readAtmAmount(resource);
        transactionService.setInitialAmount(amount);
        return RepeatStatus.FINISHED;
    }
}
