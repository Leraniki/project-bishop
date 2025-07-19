package com.weyland.yutani.core.audit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class AuditAspect {

    private final AuditService auditService;

    @Around("@annotation(com.weyland.yutani.core.audit.WeylandWatchingYou)")
    public Object auditMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().toShortString();
        Object[] args = joinPoint.getArgs();

        String auditMessageStart = String.format("Вызов метода: %s. Аргументы: %s", methodName, Arrays.toString(args));
        auditService.log(auditMessageStart);

        Object result;
        try {
            result = joinPoint.proceed();

            String auditMessageEnd = String.format("Метод %s успешно выполнен. Результат: %s", methodName, result);
            auditService.log(auditMessageEnd);

            return result;
        } catch (Throwable e) {
            String auditMessageError = String.format("Ошибка при выполнении метода %s: %s", methodName, e.getMessage());
            auditService.log(auditMessageError);

            throw e;
        }
    }
}