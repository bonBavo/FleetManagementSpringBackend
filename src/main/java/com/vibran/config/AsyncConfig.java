package com.vibran.config;
// Required for @Async on NotificationService.sendAlertNotification()
// Without this, @Async runs on the same thread — blocking MQTT processing

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "notificationExecutor")
    public Executor notificationExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);       // 4 threads always ready
        executor.setMaxPoolSize(10);       // burst up to 10
        executor.setQueueCapacity(100);    // queue 100 notifications
        executor.setThreadNamePrefix("notification-");
        executor.initialize();
        return executor;
    }
}
