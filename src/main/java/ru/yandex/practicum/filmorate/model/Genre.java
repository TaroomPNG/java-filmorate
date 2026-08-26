package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Genre {
  public static final Genre COMEDY = new Genre(1, "Комедия");
  public static final Genre DRAMA = new Genre(2, "Драма");
  public static final Genre CARTOON = new Genre(3, "Мультфильм");
  public static final Genre THRILLER = new Genre(4, "Триллер");
  public static final Genre DOCUMENTARY = new Genre(5, "Документальный");
  public static final Genre ACTION_MOVIE = new Genre(6, "Боевик");

  private Integer id;
  private String name;
}
