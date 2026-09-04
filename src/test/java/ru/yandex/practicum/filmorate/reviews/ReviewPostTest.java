package ru.yandex.practicum.filmorate.reviews;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.DaoTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;

@DisplayName("Тесты создания отзывов")
class ReviewPostTest extends DaoTest {

  @Test
  @DisplayName("Должен корректно добавить отзыв с useful = 0")
  void shouldAddCorrectReview() {
    User user = addUser("review@test.ru", "reviewUser");
    Film film = addFilm("reviewFilm");

    Review review = addReview("Great movie", true, user.getId(), film.getId());

    assertThat(review)
        .hasFieldOrPropertyWithValue("content", "Great movie")
        .hasFieldOrPropertyWithValue("isPositive", true)
        .hasFieldOrPropertyWithValue("userId", user.getId())
        .hasFieldOrPropertyWithValue("filmId", film.getId())
        .hasFieldOrPropertyWithValue("useful", 0);
  }

  @Test
  @DisplayName("Должен назначать последовательные идентификаторы отзывам")
  void shouldAddTwoReviews() {
    User user = addUser("review2@test.ru", "reviewUser2");
    Film film = addFilm("reviewFilm2");

    Review first = addReview("first", true, user.getId(), film.getId());
    Review second = addReview("second", false, user.getId(), film.getId());

    assertThat(first.getId()).isNotNull();
    assertThat(second.getId()).isEqualTo(first.getId() + 1);
  }
}
