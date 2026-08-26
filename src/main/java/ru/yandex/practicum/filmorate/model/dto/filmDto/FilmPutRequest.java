package ru.yandex.practicum.filmorate.model.dto.filmDto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;

@Data
@Builder
public class FilmPutRequest {
  @NotNull(message = "ID обязателен")
  private Long id;

  @Builder.Default private String name = null;

  @Size(max = 200, message = "Описание не может быть длиннее 200 символов")
  @Builder.Default
  private String description = null;

  @JsonFormat(pattern = "yyyy-MM-dd")
  @Builder.Default
  private LocalDate releaseDate = null;

  @Positive(message = "Длительность должна быть положительным числом")
  @Builder.Default
  private Integer duration = null;

  @JsonDeserialize(as = LinkedHashSet.class)
  @Builder.Default
  private Set<Genre> genres = null;

  @Builder.Default private Rating mpa = null;
}
