package ru.yandex.practicum.filmorate.reviews;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.DaoTest;
import ru.yandex.practicum.filmorate.controller.exceptions.ReviewNotFound;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;

@DisplayName("Тесты получения отзывов")
class ReviewGetTest extends DaoTest {

  @Test
  @DisplayName("Должен возвращать отзыв по идентификатору")
  void shouldGetReviewById() {
    User user = addUser("get@test.ru", "getUser");
    Film film = addFilm("getFilm");
    Review created = addReview("review", true, user.getId(), film.getId());

    Review review = reviewStorage.getReviewById(created.getId());

    assertThat(review)
        .hasFieldOrPropertyWithValue("id", created.getId())
        .hasFieldOrPropertyWithValue("content", "review")
        .hasFieldOrPropertyWithValue("isPositive", true);
  }

  @Test
  @DisplayName("Должен выбрасывать ReviewNotFound для несуществующего отзыва")
  void shouldGetReviewByIdNotFound() {
    assertThatThrownBy(() -> reviewStorage.getReviewById(9999L)).isInstanceOf(ReviewNotFound.class);
  }

  @Test
  @DisplayName("Сортировка отзывов фильма по полезности по убыванию")
  void shouldGetReviewsByFilmSortedByUseful() {
    User author = addUser("author@test.ru", "author");
    User voter = addUser("voter@test.ru", "voter");
    Film film = addFilm("film");
    Review highUseful = addReview("high", true, author.getId(), film.getId());
    Review lowUseful = addReview("low", false, author.getId(), film.getId());

    reviewStorage.addLike(highUseful.getId(), voter.getId());
    reviewStorage.addDislike(lowUseful.getId(), voter.getId());

    List<Review> reviews = reviewStorage.getReviews(film.getId(), 10);

    assertThat(reviews).extracting(Review::getId).containsExactly(highUseful.getId(), lowUseful.getId());
  }

  @Test
  @DisplayName("Ограничивает количество отзывов параметром count")
  void shouldGetReviewsWithCountLimit() {
    User author = addUser("count@test.ru", "countUser");
    Film film = addFilm("countFilm");
    addReview("r1", true, author.getId(), film.getId());
    addReview("r2", true, author.getId(), film.getId());

    assertThat(reviewStorage.getReviews(film.getId(), 1)).hasSize(1);
  }
}
