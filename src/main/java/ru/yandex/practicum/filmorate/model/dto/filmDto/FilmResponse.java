package ru.yandex.practicum.filmorate.model.dto.filmDto;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.dto.userDto.UserResponse;

@Data
@Builder
@AllArgsConstructor
@Jacksonized
public class FilmResponse {
  private Long id;
  private String name;
  private String description;
  private LocalDate releaseDate;
  private Integer duration;
  @Builder.Default private final Set<UserResponse> likeIds = new LinkedHashSet<>();

  public FilmResponse(Film film) {
    this.id = film.getId();
    this.name = film.getName();
    this.description = film.getDescription();
    this.releaseDate = film.getReleaseDate();
    this.duration = film.getDuration();
    this.likeIds = film.getUserLikeResponse();
  }
}
