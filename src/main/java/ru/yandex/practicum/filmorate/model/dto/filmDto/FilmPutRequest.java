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
public class FilmPutRequest {
  @NotNull(message = "ID обязателен")
  private Long id;

  @Pattern(regexp = "^[a-zA-Z0-9а-яА-Я_\\s-]{1,200}$", message = "Некорректное название фильма")
  @Builder.Default
  private String name = null;

  @Pattern(regexp = "^[a-zA-Z0-9а-яА-Я _-]{1,200}$", message = "Некорректное описание фильма")
  @Builder.Default
  private String description = null;

  @PastOrPresent(message = "Дата релиза не может быть в будущем")
  @JsonFormat(pattern = "yyyy-MM-dd")
  @Builder.Default
  private LocalDate releaseDate = null;

  @Positive(message = "Длительность должна быть положительным числом")
  @Builder.Default
  private Integer duration = null;
}
