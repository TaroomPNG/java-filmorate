package ru.yandex.practicum.filmorate.model.dto.directorDto;

import lombok.Data;
import ru.yandex.practicum.filmorate.model.Director;

@Data
public class DirectorResponse {
  private Long id;
  private String name;

  public DirectorResponse(Director director) {
    this.id = director.getId();
    this.name = director.getName();
  }
}
