package ru.yandex.practicum.filmorate.controller.service.storage;

import java.util.List;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.dto.reviewDto.ReviewPostRequest;
import ru.yandex.practicum.filmorate.model.dto.reviewDto.ReviewPutRequest;

public interface ReviewStorage {
  Review addReview(ReviewPostRequest reviewPostRequest);

  Review updateReview(ReviewPutRequest reviewPutRequest);

  boolean deleteReview(long id);

  Review getReviewById(long id);

  List<Review> getReviews(Long filmId, int count);

  boolean isReviewExists(long id);

  Review addLike(long reviewId, long userId);

  Review addDislike(long reviewId, long userId);

  Review removeLike(long reviewId, long userId);

  Review removeDislike(long reviewId, long userId);
}
