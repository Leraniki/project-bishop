package com.weyland.yutani.bishop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
// Важно! Сканируем компоненты не только в текущем пакете, но и в пакете нашего стартера
@ComponentScan(basePackages = {"com.weyland.yutani.bishop", "com.weyland.yutani.core"})
public class BishopPrototypeApplication {
    public static void main(String[] args) {
        SpringApplication.run(BishopPrototypeApplication.class, args);
    }
}