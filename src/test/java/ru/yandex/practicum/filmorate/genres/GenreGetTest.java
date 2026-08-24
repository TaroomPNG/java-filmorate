package ru.yandex.practicum.filmorate.genres;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.DaoTest;
import ru.yandex.practicum.filmorate.controller.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

class GenreGetTest extends DaoTest {

  @Test
  void getAllGenres() {
    List<Genre> genres = filmStorage.getGenres();

    assertThat(genres).hasSize(6);
    assertThat(genres.getFirst())
        .hasFieldOrPropertyWithValue("id", 1)
        .hasFieldOrPropertyWithValue("name", "Комедия");
    assertThat(genres.getLast())
        .hasFieldOrPropertyWithValue("id", 6)
        .hasFieldOrPropertyWithValue("name", "Боевик");
  }

  @Test
  void getGenreById() {
    Genre genre = filmStorage.getGenreById(1);

    assertThat(genre)
        .hasFieldOrPropertyWithValue("id", 1)
        .hasFieldOrPropertyWithValue("name", "Комедия");
  }

  @Test
  void getGenreByIdNotFound() {
    assertThatThrownBy(() -> filmStorage.getGenreById(9999L)).isInstanceOf(NotFoundException.class);
  }

  @Test
  void isGenreExists() {
    assertThat(filmStorage.isGenreExists(1)).isTrue();
    assertThat(filmStorage.isGenreExists(9999L)).isFalse();
  }
}
