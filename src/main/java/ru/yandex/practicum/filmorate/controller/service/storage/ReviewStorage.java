package ru.yandex.practicum.filmorate.controller.service.storage;

import java.util.List;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.dto.reviewDto.ReviewPostRequest;
import ru.yandex.practicum.filmorate.model.dto.reviewDto.ReviewPutRequest;

public interface ReviewStorage {
  Review addReview(ReviewPostRequest reviewPostRequest);

  Review updateReview(ReviewPutRequest reviewPutRequest);

  boolean deleteReview(Long id);

  Review getReviewById(Long id);

  List<Review> getReviews(Long filmId, int count);

  boolean isReviewExists(Long id);

  Review addLike(Long reviewId, Long userId);

  Review addDislike(Long reviewId, Long userId);

  Review removeLike(Long reviewId, Long userId);

  Review removeDislike(Long reviewId, Long userId);

  void deleteByUserId(Long userId);

  void deleteByFilmId(Long filmId);

  void clear();
}
