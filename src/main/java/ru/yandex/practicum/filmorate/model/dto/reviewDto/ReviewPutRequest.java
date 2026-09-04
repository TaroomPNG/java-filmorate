package ru.yandex.practicum.filmorate.model.dto.reviewDto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReviewPutRequest {
  @NotNull(message = "ID отзыва обязателен")
  private Long reviewId;

  @Builder.Default private String content = null;
  @Builder.Default private Boolean isPositive = null;

  @Builder.Default
  private Long userId = null;

  @Builder.Default
  private Long filmId = null;

  @Builder.Default private Integer useful = null;
}
