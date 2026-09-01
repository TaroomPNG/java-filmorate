package ru.yandex.practicum.filmorate.model.dto.reviewDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;
import ru.yandex.practicum.filmorate.model.Review;

@Data
@Builder
@AllArgsConstructor
@Jacksonized
public class ReviewResponse {
  private Long reviewId;
  private String content;
  private Boolean isPositive;
  private Long userId;
  private Long filmId;
  private Integer useful;

  public ReviewResponse(Review review) {
    this.reviewId = review.getId();
    this.content = review.getContent();
    this.isPositive = review.getIsPositive();
    this.userId = review.getUserId();
    this.filmId = review.getFilmId();
    this.useful = review.getUseful();
  }
}
