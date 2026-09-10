package ru.yandex.practicum.filmorate.model.dto.reviewDto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReviewPostRequest {
  @Builder.Default private Long reviewId = null;

  @NotBlank(message = "Текст отзыва обязателен")
  private String content;

  @NotNull(message = "Признак позитивности обязателен")
  @JsonProperty("isPositive")
  private Boolean isPositive;

  @NotNull(message = "ID пользователя обязателен")
  private Long userId;

  @NotNull(message = "ID фильма обязателен")
  private Long filmId;

  @Builder.Default private Integer useful = null;
}
