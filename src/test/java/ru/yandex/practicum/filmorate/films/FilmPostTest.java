package ru.yandex.practicum.filmorate.films;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.DaoTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.dto.filmDto.FilmPostRequest;

class FilmPostTest extends DaoTest {

  @Test
  void addCorrectFilm() {
    Film film = addFilm("TEST", Set.of(Genre.DRAMA, Genre.COMEDY), Rating.PG);

    assertThat(film)
        .hasFieldOrPropertyWithValue("name", "TEST")
        .hasFieldOrPropertyWithValue("description", "-")
        .hasFieldOrPropertyWithValue("releaseDate", DATE)
        .hasFieldOrPropertyWithValue("duration", 120);
    assertThat(film.getMpa()).hasFieldOrPropertyWithValue("id", 2);
    assertThat(film.getGenres()).extracting(Genre::getId).containsExactlyInAnyOrder(1, 2);
  }

  @Test
  void add2CorrectFilms() {
    Film first = addFilm("TEST1");
    Film second = addFilm("TEST2");

    assertThat(first.getId()).isEqualTo(1L);
    assertThat(second.getId()).isEqualTo(2L);
  }

  @Test
  void addCorrectFilmWithoutDescription() {
    Film withoutDescription =
        filmStorage.addFilm(
            FilmPostRequest.builder()
                .name("NAME")
                .description(null)
                .releaseDate(DATE)
                .duration(120)
                .genres(Set.of(Genre.DRAMA))
                .mpa(Rating.PG)
                .build());
    Film withBlankDescription =
        filmStorage.addFilm(
            FilmPostRequest.builder()
                .name("NAME1")
                .description("  ")
                .releaseDate(DATE)
                .duration(120)
                .genres(Set.of(Genre.DRAMA))
                .mpa(Rating.PG)
                .build());

    assertThat(withoutDescription.getDescription()).isEqualTo("-");
    assertThat(withBlankDescription.getDescription()).isEqualTo("-");
  }

  @Test
  void add2FilmsWithSameName() {
    Film first = addFilm("TEST");
    Film second = addFilm("TEST");

    assertThat(first.getId()).isEqualTo(1L);
    assertThat(second.getId()).isEqualTo(2L);
    assertThat(first.getName()).isEqualTo(second.getName());
  }
}
