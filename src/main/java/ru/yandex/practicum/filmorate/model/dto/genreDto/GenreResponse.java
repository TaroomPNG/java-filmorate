package ru.yandex.practicum.filmorate.model.dto.genreDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.Genre;

@Data
@AllArgsConstructor
public class GenreResponse {
  private Integer id;
  private String name;

  public GenreResponse(Genre genre) {
    this.id = genre.getId();
    this.name = genre.getName();
  }
}
