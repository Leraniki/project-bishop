package com.weyland.yutani.core.commands.exceptions;

public class QueueFullException extends RuntimeException {
    public QueueFullException(String message) {
        super(message);
    }
}