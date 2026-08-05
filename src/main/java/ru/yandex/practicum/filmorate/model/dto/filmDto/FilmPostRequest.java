package ru.yandex.practicum.filmorate.model.dto.filmDto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FilmPostRequest {
  @NotNull(message = "Имя фильма обязательно")
  @Pattern(regexp = "^[a-zA-Z0-9а-яА-Я_-]{1,200}$", message = "Некорректное название фильма")
  private String name;

  @Pattern(regexp = "^[a-zA-Z0-9а-яА-Я _-]{1,200}$", message = "Некорректное описание фильма")
  @Builder.Default
  private String description = null;

  @NotNull(message = "Дата обязательна")
  @PastOrPresent(message = "Дата релиза не может быть в будущем")
  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDate releaseDate;

  @NotNull(message = "Длительность обязательна")
  @Positive(message = "Длительность должна быть положительным числом")
  private Integer duration;
}
