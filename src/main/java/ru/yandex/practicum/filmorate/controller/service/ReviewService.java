package ru.yandex.practicum.filmorate.controller.service;

import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controller.exceptions.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.controller.exceptions.FilmNotFound;
import ru.yandex.practicum.filmorate.controller.exceptions.ReviewNotFound;
import ru.yandex.practicum.filmorate.controller.exceptions.UserNotFound;
import ru.yandex.practicum.filmorate.controller.service.storage.FilmStorage;
import ru.yandex.practicum.filmorate.controller.service.storage.ReviewStorage;
import ru.yandex.practicum.filmorate.controller.service.storage.UserStorage;
import ru.yandex.practicum.filmorate.model.dto.reviewDto.ReviewPostRequest;
import ru.yandex.practicum.filmorate.model.dto.reviewDto.ReviewPutRequest;
import ru.yandex.practicum.filmorate.model.dto.reviewDto.ReviewResponse;

@Slf4j
@Service
public class ReviewService {

  @Autowired
  @Qualifier("ReviewDbStorage")
  private ReviewStorage reviewStorage;

  @Autowired
  @Qualifier("UserDbStorage")
  private UserStorage userStorage;

  @Autowired
  @Qualifier("FilmDbStorage")
  private FilmStorage filmStorage;

  public ReviewResponse addReview(ReviewPostRequest reviewPostRequest) {
    log.trace("Вызывается addReview");
    validateReviewContent(reviewPostRequest.getContent());
    validateUser(reviewPostRequest.getUserId());
    validateFilm(reviewPostRequest.getFilmId());
    return new ReviewResponse(reviewStorage.addReview(reviewPostRequest));
  }

  public ReviewResponse updateReview(ReviewPutRequest reviewPutRequest) {
    log.trace("Вызывается updateReview: ReviewID {}", reviewPutRequest.getReviewId());
    validateReviewExists(reviewPutRequest.getReviewId());
    if (reviewPutRequest.getContent() != null) {
      validateReviewContent(reviewPutRequest.getContent());
    }
    if (reviewPutRequest.getUserId() != null) {
      validateUser(reviewPutRequest.getUserId());
    }
    if (reviewPutRequest.getFilmId() != null) {
      validateFilm(reviewPutRequest.getFilmId());
    }
    return new ReviewResponse(reviewStorage.updateReview(reviewPutRequest));
  }

  public boolean deleteReview(Long reviewId) {
    requireNotNullId(reviewId, "ID отзыва обязателен");
    log.trace("Вызывается deleteReview: ReviewID {}", reviewId);
    validateReviewExists(reviewId);
    return reviewStorage.deleteReview(reviewId);
  }

  public ReviewResponse getReviewById(Long reviewId) {
    requireNotNullId(reviewId, "ID отзыва обязателен");
    log.trace("Вызывается getReviewById: ReviewID {}", reviewId);
    return new ReviewResponse(reviewStorage.getReviewById(reviewId));
  }

  public List<ReviewResponse> getReviews(Long filmId, int count) {
    log.trace("Вызывается getReviews: FilmID {}, count {}", filmId, count);
    if (filmId != null) {
      validateFilm(filmId);
    }
    return reviewStorage.getReviews(filmId, count).stream().map(ReviewResponse::new).toList();
  }

  public ReviewResponse addLike(Long reviewId, Long userId) {
    requireNotNullId(reviewId, "ID отзыва обязателен");
    requireNotNullId(userId, "ID пользователя обязателен");
    log.trace("Вызывается addLikeToReview: ReviewID {} - UserID {}", reviewId, userId);
    validateReviewExists(reviewId);
    validateUser(userId);
    return new ReviewResponse(reviewStorage.addLike(reviewId, userId));
  }

  public ReviewResponse addDislike(Long reviewId, Long userId) {
    requireNotNullId(reviewId, "ID отзыва обязателен");
    requireNotNullId(userId, "ID пользователя обязателен");
    log.trace("Вызывается addDislikeToReview: ReviewID {} - UserID {}", reviewId, userId);
    validateReviewExists(reviewId);
    validateUser(userId);
    return new ReviewResponse(reviewStorage.addDislike(reviewId, userId));
  }

  public ReviewResponse removeLike(Long reviewId, Long userId) {
    requireNotNullId(reviewId, "ID отзыва обязателен");
    requireNotNullId(userId, "ID пользователя обязателен");
    log.trace("Вызывается removeLikeFromReview: ReviewID {} - UserID {}", reviewId, userId);
    validateReviewExists(reviewId);
    validateUser(userId);
    return new ReviewResponse(reviewStorage.removeLike(reviewId, userId));
  }

  public ReviewResponse removeDislike(Long reviewId, Long userId) {
    requireNotNullId(reviewId, "ID отзыва обязателен");
    requireNotNullId(userId, "ID пользователя обязателен");
    log.trace("Вызывается removeDislikeFromReview: ReviewID {} - UserID {}", reviewId, userId);
    validateReviewExists(reviewId);
    validateUser(userId);
    return new ReviewResponse(reviewStorage.removeDislike(reviewId, userId));
  }

  private void validateReviewContent(String content) {
    if (content == null || content.isBlank()) {
      throw new ConditionsNotMetException("Текст отзыва не может быть пустым");
    }
  }

  private void validateReviewExists(Long reviewId) {
    requireNotNullId(reviewId, "ID отзыва обязателен");
    if (!reviewStorage.isReviewExists(reviewId)) {
      throw new ReviewNotFound(reviewId);
    }
  }

  private void validateUser(Long userId) {
    requireNotNullId(userId, "ID пользователя обязателен");
    if (!userStorage.isUserExists(userId)) {
      throw new UserNotFound(userId);
    }
  }

  private void validateFilm(Long filmId) {
    requireNotNullId(filmId, "ID фильма обязателен");
    if (!filmStorage.isFilmExists(filmId)) {
      throw new FilmNotFound(filmId);
    }
  }

  private void requireNotNullId(Long id, String message) {
    Objects.requireNonNull(id, message);
  }
}
