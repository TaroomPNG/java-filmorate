package ru.yandex.practicum.filmorate.controller.service.storage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.controller.exceptions.ReviewNotFound;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.dto.reviewDto.ReviewPostRequest;
import ru.yandex.practicum.filmorate.model.dto.reviewDto.ReviewPutRequest;

@Repository("ReviewDbStorage")
public class ReviewDbStorage extends BaseRepository<Review> implements ReviewStorage {
  private static final String REVIEW_SELECT =
      "SELECT review_id, content, is_positive, user_id, film_id, useful FROM review";
  private static final String FIND_REVIEW_BY_ID = REVIEW_SELECT + " WHERE review_id = ?";
  private static final String FIND_ALL_REVIEWS =
      REVIEW_SELECT + " ORDER BY useful DESC, review_id LIMIT ?";
  private static final String FIND_REVIEWS_BY_FILM =
      REVIEW_SELECT + " WHERE film_id = ? ORDER BY useful DESC, review_id LIMIT ?";
  private static final String ADD_REVIEW =
      "INSERT INTO review (content, is_positive, user_id, film_id, useful) VALUES (?, ?, ?, ?, 0)";
  private static final String DELETE_REVIEW_REACTIONS = "DELETE FROM review_reaction WHERE review_id = ?";
  private static final String DELETE_REVIEW = "DELETE FROM review WHERE review_id = ?";
  private static final String ADD_OR_UPDATE_REACTION =
      "MERGE INTO review_reaction (review_id, user_id, is_like) KEY (review_id, user_id) VALUES (?, ?, ?)";
  private static final String DELETE_LIKE =
      "DELETE FROM review_reaction WHERE review_id = ? AND user_id = ? AND is_like = true";
  private static final String DELETE_DISLIKE =
      "DELETE FROM review_reaction WHERE review_id = ? AND user_id = ? AND is_like = false";
  private static final String RECALCULATE_USEFUL =
      "UPDATE review SET useful = COALESCE((SELECT SUM(CASE WHEN is_like THEN 1 ELSE -1 END) "
          + "FROM review_reaction WHERE review_id = ?), 0) WHERE review_id = ?";

  public ReviewDbStorage(
      JdbcTemplate jdbc, @Qualifier("reviewRowMapper") RowMapper<Review> mapper) {
    super(jdbc, mapper);
  }

  @Override
  public Review addReview(ReviewPostRequest reviewPostRequest) {
    long reviewId =
        insert(
            ADD_REVIEW,
            reviewPostRequest.getContent(),
            reviewPostRequest.getIsPositive(),
            reviewPostRequest.getUserId(),
            reviewPostRequest.getFilmId());
    return getReviewById(reviewId);
  }

  @Override
  public Review updateReview(ReviewPutRequest reviewPutRequest) {
    if (!isReviewExists(reviewPutRequest.getReviewId())) {
      throw new ReviewNotFound(reviewPutRequest.getReviewId());
    }

    List<String> setClauses = new ArrayList<>();
    List<Object> params = new ArrayList<>();

    if (reviewPutRequest.getContent() != null) {
      setClauses.add("content = ?");
      params.add(reviewPutRequest.getContent());
    }
    if (reviewPutRequest.getIsPositive() != null) {
      setClauses.add("is_positive = ?");
      params.add(reviewPutRequest.getIsPositive());
    }
    if (reviewPutRequest.getUserId() != null) {
      setClauses.add("user_id = ?");
      params.add(reviewPutRequest.getUserId());
    }
    if (reviewPutRequest.getFilmId() != null) {
      setClauses.add("film_id = ?");
      params.add(reviewPutRequest.getFilmId());
    }

    if (!setClauses.isEmpty()) {
      String updateQuery =
          "UPDATE review SET " + String.join(", ", setClauses) + " WHERE review_id = ?";
      params.add(reviewPutRequest.getReviewId());
      update(updateQuery, params.toArray());
    }

    return getReviewById(reviewPutRequest.getReviewId());
  }

  @Override
  @Transactional
  public boolean deleteReview(Long id) {
    jdbc.update(DELETE_REVIEW_REACTIONS, id);
    return delete(DELETE_REVIEW, id);
  }

  @Override
  public Review getReviewById(Long id) {
    Optional<Review> optionalReview = findOne(FIND_REVIEW_BY_ID, id);
    if (optionalReview.isEmpty()) {
      throw new ReviewNotFound(id);
    }
    return optionalReview.get();
  }

  @Override
  public List<Review> getReviews(Long filmId, int count) {
    if (filmId == null) {
      return List.copyOf(findMany(FIND_ALL_REVIEWS, count));
    }
    return List.copyOf(findMany(FIND_REVIEWS_BY_FILM, filmId, count));
  }

  @Override
  public boolean isReviewExists(Long id) {
    return findOne(FIND_REVIEW_BY_ID, id).isPresent();
  }

  @Override
  @Transactional
  public Review addLike(Long reviewId, Long userId) {
    jdbc.update(ADD_OR_UPDATE_REACTION, reviewId, userId, true);
    recalculateUseful(reviewId);
    return getReviewById(reviewId);
  }

  @Override
  @Transactional
  public Review addDislike(Long reviewId, Long userId) {
    jdbc.update(ADD_OR_UPDATE_REACTION, reviewId, userId, false);
    recalculateUseful(reviewId);
    return getReviewById(reviewId);
  }

  @Override
  @Transactional
  public Review removeLike(Long reviewId, Long userId) {
    jdbc.update(DELETE_LIKE, reviewId, userId);
    recalculateUseful(reviewId);
    return getReviewById(reviewId);
  }

  @Override
  @Transactional
  public Review removeDislike(Long reviewId, Long userId) {
    jdbc.update(DELETE_DISLIKE, reviewId, userId);
    recalculateUseful(reviewId);
    return getReviewById(reviewId);
  }

  private void recalculateUseful(Long reviewId) {
    update(RECALCULATE_USEFUL, reviewId, reviewId);
  }
}
