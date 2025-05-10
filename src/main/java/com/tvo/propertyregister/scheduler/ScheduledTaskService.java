package com.tvo.propertyregister.scheduler;

import com.tvo.propertyregister.service.OwnerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@RequiredArgsConstructor
public class ScheduledTaskService {

    private final OwnerService ownerService;

    @Scheduled(cron = "0 * * * * *")
    public void recountDebtForDebtors() {
        log.info("Recounting debt for debtors");
        ownerService.recountDebtForDebtors();
    }
}
