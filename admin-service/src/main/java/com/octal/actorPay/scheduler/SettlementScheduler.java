package com.octal.actorPay.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SettlementScheduler {


//    @Scheduled(cron = "0 0 0 * * *") // every day midnight 12 AM
//    @Scheduled(cron = "*/10 * * * * * ")
    public void doSettlement() {
        System.out.println("Ths is settlement Process......");
    }
}
