package com.batch.atm.operator.batch;


import com.batch.atm.operator.model.UserSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class TransactionChunkWritter implements ItemWriter<UserSession> {
    @Override
    public void write(List<? extends UserSession> list) throws Exception {
        log.info("Start writting");
    }
}
