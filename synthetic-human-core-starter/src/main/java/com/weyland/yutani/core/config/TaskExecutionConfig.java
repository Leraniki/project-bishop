package com.weyland.yutani.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class TaskExecutionConfig {

    @Bean("commonCommandExecutor")
    public Executor commonCommandExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2); // Количество постоянно активных потоков
        executor.setMaxPoolSize(5); // Максимальное количество потоков
        executor.setQueueCapacity(10); // Размер очереди.
        executor.setThreadNamePrefix("Android-Worker-");
        executor.setRejectedExecutionHandler(new java.util.concurrent.ThreadPoolExecutor.AbortPolicy());
        executor.initialize();
        return executor;
    }
}