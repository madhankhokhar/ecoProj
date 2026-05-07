package com.example.ecoProj.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
@Component
public class TestScheduler {

    @Scheduled(fixedDelay = 1 * 60 * 1000)
    public void runTask() throws InterruptedException {
        System.out.println("Task Started: " + LocalDateTime.now());

        // Sleep for 2 minutes (120,000 milliseconds)
        Thread.sleep(2 * 60 * 1000);

        System.out.println("Task Finished: " + LocalDateTime.now());
    }


}
