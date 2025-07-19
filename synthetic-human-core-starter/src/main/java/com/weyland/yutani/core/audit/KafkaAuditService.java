package com.weyland.yutani.core.audit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j

@ConditionalOnProperty(name = "weyland.audit.mode", havingValue = "kafka")
public class KafkaAuditService implements AuditService {

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${weyland.audit.kafka-topic:default-audit-topic}")
    private String auditTopic;

    public KafkaAuditService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void log(String message) {
        try {
            kafkaTemplate.send(auditTopic, message);
        } catch (Exception e) {
            log.error("Не удалось отправить сообщение аудита в Kafka: {}", e.getMessage());
        }
    }
}