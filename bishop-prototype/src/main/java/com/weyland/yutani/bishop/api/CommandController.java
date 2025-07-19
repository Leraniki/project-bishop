package com.weyland.yutani.bishop.api;

import com.weyland.yutani.core.audit.WeylandWatchingYou;
import com.weyland.yutani.core.commands.CommandService;
import com.weyland.yutani.core.commands.dto.CommandDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/android")
@RequiredArgsConstructor
public class CommandController {

    private final CommandService commandService; // Инжектируем сервис из стартера

    @PostMapping("/command")
    public ResponseEntity<String> receiveCommand(@Valid @RequestBody CommandDto command) {
        commandService.processCommand(command);
        return ResponseEntity.accepted().body("Команда принята к исполнению.");
    }

    @GetMapping("/status")
    @WeylandWatchingYou // Демонстрируем работу аудита
    public ResponseEntity<String> getStatus(String requester) {
        return ResponseEntity.ok("Состояние: Online. Все системы в норме. Запрашивающий: " + requester);
    }
}