package com.weyland.yutani.core.audit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service // Говорим Spring, что это сервис (бин)
@Slf4j   // Добавляем логгер
// 🔥 МАГИЯ: Этот бин будет создан ТОЛЬКО ЕСЛИ в application.properties есть свойство
// 'weyland.audit.mode' со значением 'console', ИЛИ если этого свойства нет вообще (matchIfMissing = true)
@ConditionalOnProperty(name = "weyland.audit.mode", havingValue = "console", matchIfMissing = true)
public class ConsoleAuditService implements AuditService {

    @Override
    public void log(String message) {
        // Просто выводим сообщение аудита в стандартный лог с пометкой
        log.info("[WEYLAND-YUTANI AUDIT] {}", message);
    }
}