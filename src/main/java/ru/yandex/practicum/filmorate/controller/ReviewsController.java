package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.controller.service.ReviewService;
import ru.yandex.practicum.filmorate.model.dto.reviewDto.ReviewPostRequest;
import ru.yandex.practicum.filmorate.model.dto.reviewDto.ReviewPutRequest;
import ru.yandex.practicum.filmorate.model.dto.reviewDto.ReviewResponse;

@RestController
@Validated
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewsController {
  private final ReviewService reviewService;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public ReviewResponse addReview(@Valid @RequestBody ReviewPostRequest reviewPostRequest) {
    return reviewService.addReview(reviewPostRequest);
  }

  @PutMapping
  @ResponseStatus(HttpStatus.OK)
  public ReviewResponse updateReview(@Valid @RequestBody ReviewPutRequest reviewPutRequest) {
    return reviewService.updateReview(reviewPutRequest);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public boolean deleteReview(@PathVariable @NotNull Long id) {
    return reviewService.deleteReview(id);
  }

  @GetMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public ReviewResponse getReviewById(@PathVariable @NotNull Long id) {
    return reviewService.getReviewById(id);
  }

  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  public Collection<ReviewResponse> getReviews(
      @RequestParam(required = false) Long filmId,
      @RequestParam(required = false, defaultValue = "10") @Positive int count) {
    return reviewService.getReviews(filmId, count);
  }

  @PutMapping("/{id}/like/{userId}")
  @ResponseStatus(HttpStatus.OK)
  public ReviewResponse addLike(
      @PathVariable @NotNull Long id, @PathVariable @NotNull Long userId) {
    return reviewService.addLike(id, userId);
  }

  @PutMapping("/{id}/dislike/{userId}")
  @ResponseStatus(HttpStatus.OK)
  public ReviewResponse addDislike(
      @PathVariable @NotNull Long id, @PathVariable @NotNull Long userId) {
    return reviewService.addDislike(id, userId);
  }

  @DeleteMapping("/{id}/like/{userId}")
  @ResponseStatus(HttpStatus.OK)
  public ReviewResponse removeLike(
      @PathVariable @NotNull Long id, @PathVariable @NotNull Long userId) {
    return reviewService.removeLike(id, userId);
  }

  @DeleteMapping("/{id}/dislike/{userId}")
  @ResponseStatus(HttpStatus.OK)
  public ReviewResponse removeDislike(
      @PathVariable @NotNull Long id, @PathVariable @NotNull Long userId) {
    return reviewService.removeDislike(id, userId);
  }
}
