package com.weyland.yutani.core.commands.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;


public record CommandDto(
        @NotBlank(message = "Описание не может быть пустым")
        @Size(max = 1000, message = "Описание не должно превышать 1000 символов")
        String description,

        @NotNull(message = "Приоритет должен быть указан")
        CommandPriority priority,

        @NotBlank(message = "Автор команды не может быть пустым")
        @Size(max = 100, message = "Имя автора не должно превышать 100 символов")
        String author,

        @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}(?:\\.\\d+)?(?:Z|[+-]\\d{2}:\\d{2})$",
                message = "Время должно соответствовать формату ISO 8601")
        String time
) {
}
