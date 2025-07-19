package com.weyland.yutani.core.error;

import com.weyland.yutani.core.commands.exceptions.QueueFullException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import java.time.Instant;
import java.util.stream.Collectors;

@ControllerAdvice // Этот класс будет ловить исключения со всего приложения
public class GlobalExceptionHandler {

    // Обработчик ошибок валидации
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String errors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        ErrorResponse response = new ErrorResponse(Instant.now(), HttpStatus.BAD_REQUEST.value(), "Ошибка валидации", errors);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // Обработчик переполнения очереди
    @ExceptionHandler(QueueFullException.class)
    public ResponseEntity<ErrorResponse> handleQueueFullException(QueueFullException ex) {
        ErrorResponse response = new ErrorResponse(Instant.now(), HttpStatus.SERVICE_UNAVAILABLE.value(), "Сервис перегружен", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.SERVICE_UNAVAILABLE);
    }

    // Обработчик для всех остальных непредвиденных ошибок
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllExceptions(Exception ex) {
        ErrorResponse response = new ErrorResponse(Instant.now(), HttpStatus.INTERNAL_SERVER_ERROR.value(), "Внутренняя ошибка сервера", "Произошла непредвиденная ошибка");
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // DTO для ответа об ошибке
    private record ErrorResponse(Instant timestamp, int status, String error, String message) {}
}