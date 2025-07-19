package com.weyland.yutani.core.audit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD) // Аннотацию можно ставить только на методы
@Retention(RetentionPolicy.RUNTIME) // Аннотация будет доступна во время выполнения программы
public @interface WeylandWatchingYou {
}