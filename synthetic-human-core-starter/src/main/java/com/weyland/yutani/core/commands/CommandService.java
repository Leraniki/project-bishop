package com.weyland.yutani.core.commands;

import com.weyland.yutani.core.commands.dto.CommandDto;
import com.weyland.yutani.core.commands.exceptions.QueueFullException;
import com.weyland.yutani.core.metrics.MetricsService; // <-- ИМПОРТ НАШЕГО СЕРВИСА МЕТРИК
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;

@Service
@Slf4j
@RequiredArgsConstructor // Создаст конструктор для всех final полей
public class CommandService {

    // Внедряем наш пул потоков для COMMON команд
    @Qualifier("commonCommandExecutor")
    private final Executor commonCommandExecutor;

    // 🔥 НОВОЕ: Внедряем наш сервис для сбора метрик
    private final MetricsService metricsService;

    /**
     * Основной метод для обработки входящих команд.
     * Распределяет команды в зависимости от их приоритета.
     * @param command Объект команды для обработки.
     */
    public void processCommand(CommandDto command) {
        log.info("Получена новая команда от '{}' с приоритетом {}. Описание: '{}'",
                command.author(), command.priority(), command.description());

        switch (command.priority()) {
            case CRITICAL -> executeCriticalCommand(command);
            case COMMON -> enqueueCommonCommand(command);
        }
    }

    /**
     * Немедленно выполняет команду с высоким приоритетом.
     * @param command CRITICAL команда.
     */
    private void executeCriticalCommand(CommandDto command) {
        log.warn("[CRITICAL EXECUTION] Начато выполнение критической команды от '{}'.", command.author());

        // Здесь в будущем будет реальная логика выполнения команды.
        // Сейчас мы просто логируем и симулируем завершение.

        log.warn("[CRITICAL EXECUTION] Критическая команда от '{}' выполнена.", command.author());

        // 🔥 НОВОЕ: Сообщаем сервису метрик, что еще одна задача выполнена.
        // Он увеличит счетчик для автора этой команды.
        metricsService.incrementCompletedTasks(command);
    }

    /**
     * Добавляет команду с обычным приоритетом в очередь на выполнение.
     * @param command COMMON команда.
     * @throws QueueFullException если очередь задач переполнена.
     */
    private void enqueueCommonCommand(CommandDto command) {
        try {
            // Передаем задачу в наш пул потоков.
            // Он сам поставит ее в очередь и выполнит, когда освободится рабочий поток.
            commonCommandExecutor.execute(() -> {
                log.info("[COMMON QUEUE] Начато выполнение команды из очереди от '{}'.", command.author());

                // Имитация длительной работы
                try {
                    Thread.sleep(5000); // Симулируем работу на 5 секунд
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    log.error("Выполнение команды от '{}' было прервано.", command.author());
                }

                log.info("[COMMON QUEUE] Команда из очереди от '{}' выполнена.", command.author());

                // 🔥 НОВОЕ: После успешного выполнения задачи из очереди
                // также сообщаем об этом сервису метрик.
                metricsService.incrementCompletedTasks(command);
            });
        } catch (RejectedExecutionException e) {
            // Эта ошибка возникает, когда пул потоков и его очередь переполнены
            // и он не может принять новую задачу (согласно нашей AbortPolicy).
            log.error("Очередь команд переполнена! Команда от '{}' отклонена.", command.author());
            throw new QueueFullException("Невозможно добавить команду в очередь. Ресурсы андроида исчерпаны.");
        }
    }
}