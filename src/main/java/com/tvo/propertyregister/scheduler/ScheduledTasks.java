package com.tvo.propertyregister.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class ScheduledTasks {

    @Scheduled(cron = "0 * * * * *")
    public void recountDebtForDebtors() {

    }
}
