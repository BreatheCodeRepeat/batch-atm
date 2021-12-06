package com.batch.atm.operator.batch;

import com.batch.atm.operator.model.UserSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;


@Component
@Slf4j
public class TransactionChunkProcessor implements ItemProcessor<UserSession,UserSession> {
    @Override
    public UserSession process(UserSession userSession) throws Exception {
        log.info("Started process");
         log.info(userSession.toString());
         return userSession;
    }
}
