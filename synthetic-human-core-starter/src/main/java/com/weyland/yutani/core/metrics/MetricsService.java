package com.weyland.yutani.core.metrics;

import com.weyland.yutani.core.commands.dto.CommandDto;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor; // <-- Убедитесь, что импорт правильный
import org.springframework.stereotype.Service;

@Service
public class MetricsService {
    private final MeterRegistry meterRegistry;
    private final ThreadPoolTaskExecutor commonCommandExecutor; // <-- ИЗМЕНЕНИЕ №1: Указываем конкретный тип

    // Внедряем бин по имени, чтобы не было путаницы
    public MetricsService(MeterRegistry meterRegistry,
                          @Qualifier("commonCommandExecutor") ThreadPoolTaskExecutor commonCommandExecutor) {
        this.meterRegistry = meterRegistry;
        this.commonCommandExecutor = commonCommandExecutor; // Сохраняем для дальнейшего использования

        // Метрика: текущий размер очереди
        // Теперь мы используем правильную цепочку вызовов
        meterRegistry.gauge(
                "android.tasks.in_queue",
                this.commonCommandExecutor, // Передаем сам executor
                // ИЗМЕНЕНИЕ №2: Правильный путь к размеру очереди
                executor -> (double) executor.getThreadPoolExecutor().getQueue().size()
        );
    }

    public void incrementCompletedTasks(CommandDto command) {
        // Метрика: счетчик выполненных задач с тегом "author"
        Counter.builder("android.tasks.completed")
                .tag("author", command.author())
                .description("Количество выполненных заданий для каждого автора")
                .register(meterRegistry)
                .increment();
    }
}