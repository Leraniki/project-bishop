package com.weyland.yutani.core.commands;

import com.weyland.yutani.core.commands.dto.CommandDto;
import com.weyland.yutani.core.commands.exceptions.QueueFullException;
import com.weyland.yutani.core.metrics.MetricsService;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommandService {

    @Qualifier("commonCommandExecutor")
    private final Executor commonCommandExecutor;

    private final MetricsService metricsService;


    public void processCommand(CommandDto command) {
        log.info("Получена новая команда от '{}' с приоритетом {}. Описание: '{}'",
                command.author(), command.priority(), command.description());

        switch (command.priority()) {
            case CRITICAL -> executeCriticalCommand(command);
            case COMMON -> enqueueCommonCommand(command);
        }
    }


    private void executeCriticalCommand(CommandDto command) {
        log.warn("[CRITICAL EXECUTION] Начато выполнение критической команды от '{}'.", command.author());


        log.warn("[CRITICAL EXECUTION] Критическая команда от '{}' выполнена.", command.author());


        metricsService.incrementCompletedTasks(command);
    }


    private void enqueueCommonCommand(CommandDto command) {
        try {

            commonCommandExecutor.execute(() -> {
                log.info("[COMMON QUEUE] Начато выполнение команды из очереди от '{}'.", command.author());

                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    log.error("Выполнение команды от '{}' было прервано.", command.author());
                }

                log.info("[COMMON QUEUE] Команда из очереди от '{}' выполнена.", command.author());


                metricsService.incrementCompletedTasks(command);
            });
        } catch (RejectedExecutionException e) {

            log.error("Очередь команд переполнена! Команда от '{}' отклонена.", command.author());
            throw new QueueFullException("Невозможно добавить команду в очередь. Ресурсы андроида исчерпаны.");
        }
    }
}