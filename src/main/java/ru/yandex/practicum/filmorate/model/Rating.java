package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@AllArgsConstructor
public enum Rating {
  G("G"),
  PG("PG"),
  PG_13("PG-13"),
  R("R"),
  NC_17("NC-17");

  private final String value;
}
