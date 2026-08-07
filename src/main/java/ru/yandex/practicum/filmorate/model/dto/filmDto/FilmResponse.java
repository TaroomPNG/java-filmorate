package ru.yandex.practicum.filmorate.model.dto.filmDto;

import java.time.LocalDate;
import java.util.Set;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.dto.userDto.UserResponse;

@Data
@Builder
public class FilmResponse {
  private Long id;
  private String name;
  private String description;
  private LocalDate releaseDate;
  private Integer duration;
  private final Set<UserResponse> likeIds;

  public FilmResponse(Film film) {
    this.id = film.getId();
    this.name = film.getName();
    this.description = film.getDescription();
    this.releaseDate = film.getReleaseDate();
    this.duration = film.getDuration();
    this.likeIds = film.getUserLikeResponse();
  }
}
