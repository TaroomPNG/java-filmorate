package ru.yandex.practicum.filmorate.films;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.DaoTest;
import ru.yandex.practicum.filmorate.model.Film;

class FilmDeleteTest extends DaoTest {

  @Test
  void deleteCorrectFilm() {
    Film created = addFilm("DEL");

    assertThat(filmStorage.deleteFilm(created.getId())).isTrue();
    assertThat(filmStorage.isFilmExists(created.getId())).isFalse();
  }

  @Test
  void deleteNonexistentFilm() {
    assertThat(filmStorage.deleteFilm(9999L)).isFalse();
  }
}
