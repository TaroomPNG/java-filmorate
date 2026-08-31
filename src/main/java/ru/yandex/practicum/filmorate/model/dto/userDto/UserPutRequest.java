package ru.yandex.practicum.filmorate.model.dto.userDto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
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

  @Pattern(regexp = "\\S+", message = "Login не должен содержать пробелы")
  @Builder.Default
  private String login = null;

  @PastOrPresent(message = "Дата рождения не может быть в будущем")
  @JsonFormat(pattern = "yyyy-MM-dd")
  @Builder.Default
  private LocalDate birthday = null;

  @Builder.Default private String name = null;
}
