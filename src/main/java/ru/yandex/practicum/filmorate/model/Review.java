package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Review {
  private Long id;
  private String content;
  private Boolean isPositive;
  private Long userId;
  private Long filmId;
  private Integer useful;
}
