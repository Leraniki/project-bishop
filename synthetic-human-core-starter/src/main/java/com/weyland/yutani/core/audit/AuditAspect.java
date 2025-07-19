package com.weyland.yutani.core.audit; // Пакет, в котором мы находимся

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect      // 1. Говорит Spring: "Я - аспект, я содержу 'советы' (advices)"
@Component   // 2. Говорит Spring: "Создай мой экземпляр и управляй им (сделай меня бином)"
@Slf4j       // 3. Аннотация Lombok для автоматического создания логгера (private static final Logger log = ...)
@RequiredArgsConstructor // 4. Аннотация Lombok для создания конструктора для final полей
public class AuditAspect {

    // 5. Внедряем наш сервис для хранения записей. Spring сам подставит нужную реализацию (консоль или Kafka)
    private final AuditService auditService;

    // 6. Это "совет" (advice). Он выполняется "вокруг" (Around) любого метода,
    //    помеченного нашей аннотацией @WeylandWatchingYou
    @Around("@annotation(com.weyland.yutani.core.audit.WeylandWatchingYou)")
    public Object auditMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        // -- Код, который выполняется ДО вызова основного метода --
        String methodName = joinPoint.getSignature().toShortString(); // Получаем имя метода
        Object[] args = joinPoint.getArgs(); // Получаем аргументы, с которыми его вызвали

        String auditMessageStart = String.format("Вызов метода: %s. Аргументы: %s", methodName, Arrays.toString(args));
        auditService.log(auditMessageStart);

        Object result;
        try {
            // -- 7. ВАЖНЕЙШИЙ МОМЕНТ: Вызываем оригинальный, "перехваченный" метод --
            result = joinPoint.proceed();

            // -- Код, который выполняется ПОСЛЕ успешного вызова основного метода --
            String auditMessageEnd = String.format("Метод %s успешно выполнен. Результат: %s", methodName, result);
            auditService.log(auditMessageEnd);

            return result; // Возвращаем результат работы оригинального метода
        } catch (Throwable e) {
            // -- Ко-д, который выполняется, если оригинальный метод выбросил ошибку --
            String auditMessageError = String.format("Ошибка при выполнении метода %s: %s", methodName, e.getMessage());
            auditService.log(auditMessageError);

            throw e; // ОБЯЗАТЕЛЬНО пробрасываем ошибку дальше, чтобы не сломать логику приложения
        }
    }
}