package ru.yandex.practicum.filmorate.model.dto.filmDto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;

@Data
@Builder
public class FilmPostRequest {
  @NotBlank(message = "Имя фильма обязательно")
  private String name;

  @Size(max = 200, message = "Описание не может быть длиннее 200 символов")
  @Builder.Default
  private String description = "-";

  @NotNull(message = "Дата обязательна")
  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDate releaseDate;

  @NotNull(message = "Длительность обязательна")
  @Positive(message = "Длительность должна быть положительным числом")
  private Integer duration;

  @JsonDeserialize(as = LinkedHashSet.class)
  @Builder.Default
  private Set<Genre> genres = new LinkedHashSet<>();

  @NotNull(message = "Рейтинг обязателен")
  private Rating mpa;

  @JsonDeserialize(as = LinkedHashSet.class)
  @Builder.Default
  private Set<Director> directors = new LinkedHashSet<>();
}
