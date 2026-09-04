package ru.yandex.practicum.filmorate.reviews;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.DaoTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.dto.reviewDto.ReviewPutRequest;

@DisplayName("Тесты обновления отзывов")
class ReviewPutTest extends DaoTest {

  @Test
  @DisplayName("Должен обновлять текст и признак позитивности отзыва")
  void shouldUpdateReviewContentAndPositiveFlag() {
    User user = addUser("put@test.ru", "putUser");
    Film film = addFilm("putFilm");
    Review created = addReview("before", false, user.getId(), film.getId());

    Review updated =
        reviewStorage.updateReview(
            ReviewPutRequest.builder()
                .reviewId(created.getId())
                .content("after")
                .isPositive(true)
                .build());

    assertThat(updated)
        .hasFieldOrPropertyWithValue("id", created.getId())
        .hasFieldOrPropertyWithValue("content", "after")
        .hasFieldOrPropertyWithValue("isPositive", true)
        .hasFieldOrPropertyWithValue("useful", 0);
  }

  @Test
  @DisplayName("Должен обновлять автора и фильм у отзыва")
  void shouldUpdateReviewUserAndFilm() {
    User firstUser = addUser("first@test.ru", "first");
    User secondUser = addUser("second@test.ru", "second");
    Film firstFilm = addFilm("firstFilm");
    Film secondFilm = addFilm("secondFilm");
    Review created = addReview("review", true, firstUser.getId(), firstFilm.getId());

    Review updated =
        reviewStorage.updateReview(
            ReviewPutRequest.builder()
                .reviewId(created.getId())
                .userId(secondUser.getId())
                .filmId(secondFilm.getId())
                .build());

    assertThat(updated)
        .hasFieldOrPropertyWithValue("userId", secondUser.getId())
        .hasFieldOrPropertyWithValue("filmId", secondFilm.getId());
  }
}
