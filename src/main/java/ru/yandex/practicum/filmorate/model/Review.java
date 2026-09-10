package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Review {
  private Long id;
  private String content;
  @JsonProperty("isPositive")
  private Boolean isPositive;
  private Long userId;
  private Long filmId;
  private Integer useful;
}
