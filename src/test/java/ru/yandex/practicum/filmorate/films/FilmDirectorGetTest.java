package ru.yandex.practicum.filmorate.films;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.DaoTest;
import ru.yandex.practicum.filmorate.controller.exceptions.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.dto.filmDto.FilmPostRequest;

class FilmDirectorGetTest extends DaoTest {

  @Test
  void addFilmWithDirector() {
    Director director = addDirector("TESTDirector");

    Film film = addFilmWithDirector("Jaws", DATE, director);

    assertThat(film.getDirectors()).extracting(Director::getId).containsExactly(director.getId());
  }

  @Test
  void getFilmsByDirectorSortedByYear() {
    Director director = addDirector("TESTDirector");
    Film newer = addFilmWithDirector("Later", LocalDate.of(2005, 1, 1), director);
    Film older = addFilmWithDirector("Earlier", LocalDate.of(1990, 1, 1), director);
    addFilm("Other");

    List<Film> films = filmStorage.getFilmsByDirector(director.getId(), "year");

    assertThat(films).extracting(Film::getId).containsExactly(older.getId(), newer.getId());
  }

  @Test
  void getFilmsByDirectorSortedByLikes() {
    Director director = addDirector("TESTDirector");
    User userOne = addUser("test@yandex.ru", "TEST");
    User userTwo = addUser("test2@yandex.ru", "TEST2");
    Film twoLikes = addFilmWithDirector("Popular", DATE, director);
    Film oneLike = addFilmWithDirector("LessPopular", DATE, director);
    Film noLikes = addFilmWithDirector("Unpopular", DATE, director);

    filmStorage.addLike(twoLikes.getId(), userOne.getId());
    filmStorage.addLike(twoLikes.getId(), userTwo.getId());
    filmStorage.addLike(oneLike.getId(), userOne.getId());

    List<Film> films = filmStorage.getFilmsByDirector(director.getId(), "likes");

    assertThat(films)
        .extracting(Film::getId)
        .containsExactly(twoLikes.getId(), oneLike.getId(), noLikes.getId());
  }

  @Test
  void getFilmsByDirectorEmpty() {
    Director director = addDirector("TESTDirector");
    addFilm("Other");

    assertThat(filmStorage.getFilmsByDirector(director.getId(), "year")).isEmpty();
  }

  @Test
  void getFilmsByDirectorInvalidSortBy() {
    Director director = addDirector("TESTDirector");

    assertThatThrownBy(() -> filmStorage.getFilmsByDirector(director.getId(), "rating"))
        .isInstanceOf(ConditionsNotMetException.class);
  }

  private Film addFilmWithDirector(String name, LocalDate releaseDate, Director director) {
    return filmStorage.addFilm(
        FilmPostRequest.builder()
            .name(name)
            .releaseDate(releaseDate)
            .duration(120)
            .genres(Set.of(Genre.DRAMA))
            .mpa(Rating.PG)
            .directors(Set.of(director))
            .build());
  }
}
