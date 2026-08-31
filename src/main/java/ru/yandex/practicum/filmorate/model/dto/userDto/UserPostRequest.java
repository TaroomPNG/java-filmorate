package ru.yandex.practicum.filmorate.model.dto.userDto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
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
  @Pattern(regexp = "\\S+", message = "Login не должен содержать пробелы")
  private String login;

  @NotNull(message = "Дата рождения обязательна")
  @PastOrPresent(message = "Дата рождения не может быть в будущем")
  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDate birthday;

  @Builder.Default private String name = null;
}
