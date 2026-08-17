package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@AllArgsConstructor
public enum Genre {
  COMEDY("Комедия"),
  DRAMA("Драма"),
  CARTOON("Мультфильм"),
  THRILLER("Триллер"),
  DOCUMENTARY("Документальный"),
  ACTION_MOVIE("Боевик");

  private final String displayName;
}
