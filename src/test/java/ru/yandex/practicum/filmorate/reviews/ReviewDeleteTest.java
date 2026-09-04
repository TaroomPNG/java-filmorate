package ru.yandex.practicum.filmorate.reviews;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.DaoTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;

@DisplayName("Тесты удаления отзывов")
class ReviewDeleteTest extends DaoTest {

  @Test
  @DisplayName("Должен удалять существующий отзыв")
  void shouldDeleteCorrectReview() {
    User user = addUser("delete@test.ru", "deleteUser");
    Film film = addFilm("deleteFilm");
    Review created = addReview("delete me", true, user.getId(), film.getId());

    assertThat(reviewStorage.deleteReview(created.getId())).isTrue();
    assertThat(reviewStorage.isReviewExists(created.getId())).isFalse();
  }

  @Test
  @DisplayName("Должен возвращать false при удалении несуществующего отзыва")
  void shouldDeleteNonexistentReview() {
    assertThat(reviewStorage.deleteReview(9999L)).isFalse();
  }
}
