package ru.yandex.practicum.filmorate.model.dto.filmDto;

import java.time.LocalDate;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FilmResponse {
  private Long id;
  private String name;
  private String description;
  private LocalDate releaseDate;
  private Integer duration;
}
