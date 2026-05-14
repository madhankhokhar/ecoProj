package com.example.ecoProj.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
@EnableScheduling
public class AsyncConfig {

    @Bean(name = "schedulerExecutor")
    public Executor schedulerExecutor() {

        ThreadPoolTaskExecutor executor =
                new ThreadPoolTaskExecutor();

        // Minimum threads always alive
        executor.setCorePoolSize(2);

        // Maximum allowed threads
        executor.setMaxPoolSize(5);

        // Queue size before creating extra threads
        executor.setQueueCapacity(100);

       //  Thread name prefix
        executor.setThreadNamePrefix("Scheduler-Async-");

        executor.initialize();

        return executor;
    }
    @Bean(name = "emailExecutor")
    public Executor emailExecutor() {

        ThreadPoolTaskExecutor executor =
                new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(100);

        executor.setThreadNamePrefix("Email-Thread-");

        executor.initialize();

        return executor;
    }

}