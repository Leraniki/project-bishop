package com.weyland.yutani.core.metrics;

import com.weyland.yutani.core.commands.dto.CommandDto;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

@Service
public class MetricsService {
    private final MeterRegistry meterRegistry;
    private final ThreadPoolTaskExecutor commonCommandExecutor;

    public MetricsService(MeterRegistry meterRegistry,
                          @Qualifier("commonCommandExecutor") ThreadPoolTaskExecutor commonCommandExecutor) {
        this.meterRegistry = meterRegistry;
        this.commonCommandExecutor = commonCommandExecutor;


        meterRegistry.gauge(
                "android.tasks.in_queue",
                this.commonCommandExecutor,
                executor -> (double) executor.getThreadPoolExecutor().getQueue().size()
        );
    }

    public void incrementCompletedTasks(CommandDto command) {
        Counter.builder("android.tasks.completed")
                .tag("author", command.author())
                .description("Количество выполненных заданий для каждого автора")
                .register(meterRegistry)
                .increment();
    }
}