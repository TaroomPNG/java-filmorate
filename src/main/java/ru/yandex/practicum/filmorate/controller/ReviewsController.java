package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.Collection;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<ReviewResponse> addReview(@Valid @RequestBody ReviewPostRequest reviewPostRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reviewService.addReview(reviewPostRequest));
    }

    @PutMapping
    public ResponseEntity<ReviewResponse> updateReview(@Valid @RequestBody ReviewPutRequest reviewPutRequest) {
        return ResponseEntity.ok(reviewService.updateReview(reviewPutRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteReview(@PathVariable @NotNull Long id) {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(reviewService.deleteReview(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReviewResponse> getReviewById(@PathVariable @NotNull Long id) {
        return ResponseEntity.ok(reviewService.getReviewById(id));
    }

    @GetMapping
    public ResponseEntity<Collection<ReviewResponse>> getReviews(
            @RequestParam(required = false) Long filmId,
            @RequestParam(required = false, defaultValue = "10") @Positive int count) {
        return ResponseEntity.ok(reviewService.getReviews(filmId, count));
    }

    @PutMapping("/{id}/like/{userId}")
    public ResponseEntity<ReviewResponse> addLike(
            @PathVariable @NotNull Long id, @PathVariable @NotNull Long userId) {
        return ResponseEntity.ok(reviewService.addLike(id, userId));
    }

    @PutMapping("/{id}/dislike/{userId}")
    public ResponseEntity<ReviewResponse> addDislike(
            @PathVariable @NotNull Long id, @PathVariable @NotNull Long userId) {
        return ResponseEntity.ok(reviewService.addDislike(id, userId));
    }

    @DeleteMapping("/{id}/like/{userId}")
    public ResponseEntity<ReviewResponse> removeLike(
            @PathVariable @NotNull Long id, @PathVariable @NotNull Long userId) {
        return ResponseEntity.ok(reviewService.removeLike(id, userId));
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public ResponseEntity<ReviewResponse> removeDislike(
            @PathVariable @NotNull Long id, @PathVariable @NotNull Long userId) {
        return ResponseEntity.ok(reviewService.removeDislike(id, userId));
    }
}
