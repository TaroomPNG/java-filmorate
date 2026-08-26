package ru.yandex.practicum.filmorate.films;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.DaoTest;
import ru.yandex.practicum.filmorate.controller.exceptions.FilmNotFound;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;

class FilmGetTest extends DaoTest {

  @Test
  void getOneFilm() {
    Film created = addFilm("TEST");

    List<Film> films = filmStorage.getFilms();

    assertThat(films).hasSize(1);
    assertThat(films.getFirst())
        .hasFieldOrPropertyWithValue("id", created.getId())
        .hasFieldOrPropertyWithValue("name", "TEST")
        .hasFieldOrPropertyWithValue("description", "-")
        .hasFieldOrPropertyWithValue("releaseDate", DATE)
        .hasFieldOrPropertyWithValue("duration", 120);
    assertThat(films.getFirst().getMpa()).hasFieldOrPropertyWithValue("id", 2);
    assertThat(films.getFirst().getGenres()).hasSize(2);
  }

  @Test
  void getManyFilms() {
    addFilm("TEST");
    addFilm("TEST1");
    addFilm("TEST2");

    List<Film> films = filmStorage.getFilms();

    assertThat(films).extracting(Film::getId).containsExactly(1L, 2L, 3L);
  }

  @Test
  void getFilmById() {
    Film created = addFilm("TEST", Set.of(Genre.DRAMA, Genre.COMEDY), Rating.PG);

    Film film = filmStorage.getFilmById(created.getId());

    assertThat(film)
        .hasFieldOrPropertyWithValue("id", created.getId())
        .hasFieldOrPropertyWithValue("name", "TEST");
    assertThat(film.getMpa()).hasFieldOrPropertyWithValue("id", 2);
    assertThat(film.getMpa()).hasFieldOrPropertyWithValue("name", "PG");
    assertThat(film.getGenres()).extracting(Genre::getId).containsExactlyInAnyOrder(1, 2);
  }

  @Test
  void getFilmByIdNotFound() {
    assertThatThrownBy(() -> filmStorage.getFilmById(9999L)).isInstanceOf(FilmNotFound.class);
  }

  @Test
  void isFilmExists() {
    Film created = addFilm("TEST");

    assertThat(filmStorage.isFilmExists(created.getId())).isTrue();
    assertThat(filmStorage.isFilmExists(9999L)).isFalse();
  }

  @Test
  void getFilmGenres() {
    Film created = addFilm("GENRES", Set.of(Genre.DRAMA, Genre.COMEDY), Rating.PG);

    assertThat(filmStorage.getFilmGenres(created.getId()))
        .extracting(Genre::getId)
        .containsExactly(1, 2);
  }
}
