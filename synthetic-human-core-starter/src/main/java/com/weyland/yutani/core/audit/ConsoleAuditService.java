package com.weyland.yutani.core.audit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@Slf4j

@ConditionalOnProperty(name = "weyland.audit.mode", havingValue = "console", matchIfMissing = true)
public class ConsoleAuditService implements AuditService {

    @Override
    public void log(String message) {
        log.info("[WEYLAND-YUTANI AUDIT] {}", message);
    }
}