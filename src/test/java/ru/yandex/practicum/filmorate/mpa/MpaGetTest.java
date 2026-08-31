package ru.yandex.practicum.filmorate.mpa;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.DaoTest;
import ru.yandex.practicum.filmorate.controller.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Rating;

class MpaGetTest extends DaoTest {

  @Test
  void getAllRatings() {
    List<Rating> ratings = filmStorage.getRatings();

    assertThat(ratings).hasSize(5);
    assertThat(ratings).extracting(Rating::getId).containsExactly(1, 2, 3, 4, 5);
    assertThat(ratings.getFirst()).hasFieldOrPropertyWithValue("name", "G");
    assertThat(ratings.getLast()).hasFieldOrPropertyWithValue("name", "NC-17");
  }

  @Test
  void getRatingById() {
    Rating rating = filmStorage.getRatingById(2);

    assertThat(rating)
        .hasFieldOrPropertyWithValue("id", 2)
        .hasFieldOrPropertyWithValue("name", "PG");
  }

  @Test
  void getRatingByIdNotFound() {
    assertThatThrownBy(() -> filmStorage.getRatingById(9999L))
        .isInstanceOf(NotFoundException.class);
  }

  @Test
  void isRatingExists() {
    assertThat(filmStorage.isRatingExists(1)).isTrue();
    assertThat(filmStorage.isRatingExists(9999L)).isFalse();
  }
}
