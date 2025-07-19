package com.weyland.yutani.core.audit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service // Говорим Spring, что это сервис (бин)
@Slf4j
// 🔥 МАГИЯ: Этот бин будет создан ТОЛЬКО ЕСЛИ в application.properties есть свойство
// 'weyland.audit.mode' со значением 'kafka'
@ConditionalOnProperty(name = "weyland.audit.mode", havingValue = "kafka")
public class KafkaAuditService implements AuditService {

    // Внедряем стандартный инструмент Spring для отправки сообщений в Kafka
    private final KafkaTemplate<String, String> kafkaTemplate;

    // Внедряем имя топика из application.properties (например, weyland.audit.kafka-topic=audit-logs)
    @Value("${weyland.audit.kafka-topic:default-audit-topic}")
    private String auditTopic;

    public KafkaAuditService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void log(String message) {
        try {
            // Отправляем сообщение в Kafka
            kafkaTemplate.send(auditTopic, message);
        } catch (Exception e) {
            // Если Kafka недоступна, логируем ошибку, чтобы не уронить все приложение
            log.error("Не удалось отправить сообщение аудита в Kafka: {}", e.getMessage());
        }
    }
}