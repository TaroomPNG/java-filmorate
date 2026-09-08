package ru.yandex.practicum.filmorate.controller.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.controller.exceptions.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.controller.exceptions.FilmNotFound;
import ru.yandex.practicum.filmorate.controller.exceptions.ReviewNotFound;
import ru.yandex.practicum.filmorate.controller.exceptions.UserNotFound;
import ru.yandex.practicum.filmorate.controller.service.storage.FilmStorage;
import ru.yandex.practicum.filmorate.controller.service.storage.ReviewStorage;
import ru.yandex.practicum.filmorate.controller.service.storage.UserStorage;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.model.dto.feedDto.FeedPostRequest;
import ru.yandex.practicum.filmorate.model.dto.reviewDto.ReviewPostRequest;
import ru.yandex.practicum.filmorate.model.dto.reviewDto.ReviewPutRequest;
import ru.yandex.practicum.filmorate.model.dto.reviewDto.ReviewResponse;

@Slf4j
@Service
@Validated
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

  @Autowired private FeedService feedService;

  public ReviewResponse addReview(@Valid @NotNull ReviewPostRequest reviewPostRequest) {
    log.trace("Вызывается addReview");
    validateReviewContent(reviewPostRequest.getContent());
    validateUser(reviewPostRequest.getUserId());
    validateFilm(reviewPostRequest.getFilmId());

    ReviewResponse created = new ReviewResponse(reviewStorage.addReview(reviewPostRequest));
    feedService.addToFeed(
        new FeedPostRequest(
            created.getUserId(), EventType.REVIEW, Operation.ADD, created.getReviewId()));
    return created;
  }

  public ReviewResponse updateReview(@Valid @NotNull ReviewPutRequest reviewPutRequest) {
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

    ReviewResponse updated = new ReviewResponse(reviewStorage.updateReview(reviewPutRequest));
    feedService.addToFeed(
        new FeedPostRequest(
            updated.getUserId(), EventType.REVIEW, Operation.UPDATE, updated.getReviewId()));
    return updated;
  }

  public boolean deleteReview(@NotNull Long reviewId) {
    log.trace("Вызывается deleteReview: ReviewID {}", reviewId);
    validateReviewExists(reviewId);

    Long userId = getReviewById(reviewId).getUserId();
    boolean isDeleted = reviewStorage.deleteReview(reviewId);
    feedService.addToFeed(
        new FeedPostRequest(userId, EventType.REVIEW, Operation.REMOVE, reviewId));

    return isDeleted;
  }

  public ReviewResponse getReviewById(@NotNull Long reviewId) {
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

  public ReviewResponse addLike(@NotNull Long reviewId, @NotNull Long userId) {
    log.trace("Вызывается addLikeToReview: ReviewID {} - UserID {}", reviewId, userId);
    validateReviewExists(reviewId);
    validateUser(userId);
    return new ReviewResponse(reviewStorage.addLike(reviewId, userId));
  }

  public ReviewResponse addDislike(@NotNull Long reviewId, @NotNull Long userId) {
    log.trace("Вызывается addDislikeToReview: ReviewID {} - UserID {}", reviewId, userId);
    validateReviewExists(reviewId);
    validateUser(userId);
    return new ReviewResponse(reviewStorage.addDislike(reviewId, userId));
  }

  public ReviewResponse removeLike(@NotNull Long reviewId, @NotNull Long userId) {
    log.trace("Вызывается removeLikeFromReview: ReviewID {} - UserID {}", reviewId, userId);
    validateReviewExists(reviewId);
    validateUser(userId);
    return new ReviewResponse(reviewStorage.removeLike(reviewId, userId));
  }

  public ReviewResponse removeDislike(@NotNull Long reviewId, @NotNull Long userId) {
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
    if (!reviewStorage.isReviewExists(reviewId)) {
      throw new ReviewNotFound(reviewId);
    }
  }

  private void validateUser(Long userId) {
    if (!userStorage.isUserExists(userId)) {
      throw new UserNotFound(userId);
    }
  }

  private void validateFilm(Long filmId) {
    if (!filmStorage.isFilmExists(filmId)) {
      throw new FilmNotFound(filmId);
    }
  }
}
