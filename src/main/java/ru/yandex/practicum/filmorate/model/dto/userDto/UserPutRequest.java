package ru.yandex.practicum.filmorate.model.dto.userDto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserPutRequest {

  @NotNull(message = "ID Обязателен для ввода")
  private Long id;

  @Email(message = "Некорректный email")
  @Builder.Default
  private String email = null;

  @Pattern(regexp = "^[a-zA-Z0-9а-яА-Я_-]{4,15}$", message = "Некорректный login")
  @Builder.Default
  private String login = null;

  @PastOrPresent(message = "Дата рождения не может быть в будущем")
  @JsonFormat(pattern = "yyyy-MM-dd")
  @Builder.Default
  private LocalDate birthday = null;

  @Pattern(regexp = "^[a-zA-Z0-9а-яА-Я _-]{1,25}$", message = "Некорректный имя")
  @Builder.Default
  private String name = null;
}
