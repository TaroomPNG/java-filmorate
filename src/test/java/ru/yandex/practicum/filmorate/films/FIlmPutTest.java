package ru.yandex.practicum.filmorate.films;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Set;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.DaoTest;
import ru.yandex.practicum.filmorate.controller.exceptions.FilmNotFound;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.dto.filmDto.FilmPostRequest;
import ru.yandex.practicum.filmorate.model.dto.filmDto.FilmPutRequest;

class FIlmPutTest extends DaoTest {

  @Test
  void putCorrectFilm() {
    Film created = addFilm("TEST");

    Film updated =
        filmStorage.updateFilm(
            FilmPutRequest.builder().id(created.getId()).name("TEST_CHANGE").build());

    assertThat(updated)
        .hasFieldOrPropertyWithValue("name", "TEST_CHANGE")
        .hasFieldOrPropertyWithValue("description", created.getDescription())
        .hasFieldOrPropertyWithValue("releaseDate", created.getReleaseDate())
        .hasFieldOrPropertyWithValue("duration", created.getDuration());
  }

  @Test
  void putIncorrectFilmId() {
    assertThatThrownBy(() -> filmStorage.updateFilm(FilmPutRequest.builder().id(2L).build()))
        .isInstanceOf(FilmNotFound.class);
  }

  @Test
  void putFilmWithSameName() {
    Film created = addFilm("TEST");

    Film updated =
        filmStorage.updateFilm(
            FilmPutRequest.builder().id(created.getId()).name(created.getName()).build());

    assertThat(updated.getName()).isEqualTo("TEST");
  }

  @Test
  void putFilmGenresAndMpa() {
    Film created = addFilm("OLD", Set.of(Genre.DRAMA), Rating.G);

    Film updated =
        filmStorage.updateFilm(
            FilmPutRequest.builder()
                .id(created.getId())
                .mpa(Rating.R)
                .genres(Set.of(Genre.THRILLER))
                .build());

    assertThat(updated.getMpa().getId()).isEqualTo(4);
    assertThat(updated.getGenres()).extracting(Genre::getId).containsExactly(4);
  }

  @Test
  void putFilmWithoutDirectorsClearsDirectors() {
    Director director = addDirector("TESTDirector");
    Film created =
        filmStorage.addFilm(
            FilmPostRequest.builder()
                .name("TEST")
                .releaseDate(DATE)
                .duration(120)
                .mpa(Rating.PG)
                .directors(Set.of(director))
                .build());

    Film updated =
        filmStorage.updateFilm(FilmPutRequest.builder().id(created.getId()).name("TEST").build());

    assertThat(updated.getDirectors()).isEmpty();
  }
}
