package ru.yandex.practicum.filmorate.model.dto.userDto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserPostRequest {
  @Email(message = "Некорректный email")
  @NotBlank(message = "Email не введен")
  private String email;

  @NotBlank(message = "Login обязателен")
  @Pattern(regexp = "^[a-zA-Z0-9а-яА-Я_-]{4,15}$", message = "Некорректный login")
  private String login;

  @NotNull(message = "Дата рождения обязательна")
  @PastOrPresent(message = "Дата рождения не может быть в будущем")
  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDate birthday;

  @Pattern(regexp = "^[a-zA-Z0-9а-яА-Я _-]{1,25}$", message = "Некорректный имя")
  @Builder.Default
  private String name = null;
}
