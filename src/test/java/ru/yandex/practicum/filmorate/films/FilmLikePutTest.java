package ru.yandex.practicum.filmorate.films;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.DaoTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

class FilmLikePutTest extends DaoTest {

  @Test
  void addCorrectLike() {
    Film film = addFilm("TEST");
    User user = addUser("test@yandex.ru", "TEST");

    filmStorage.addLike(film.getId(), user.getId());

    assertThat(filmStorage.getFilmById(film.getId()).getLikeIds())
        .extracting(User::getId)
        .containsExactly(user.getId());
  }

  @Test
  void addLikeTwiceIsIdempotent() {
    Film film = addFilm("TEST");
    User user = addUser("test@yandex.ru", "TEST");

    filmStorage.addLike(film.getId(), user.getId());
    filmStorage.addLike(film.getId(), user.getId());

    assertThat(filmStorage.getFilmById(film.getId()).getLikeIds()).hasSize(1);
  }

  @Test
  void addLikeFromMultipleUsers() {
    Film film = addFilm("TEST");
    User userOne = addUser("test@yandex.ru", "TEST");
    User userTwo = addUser("test2@yandex.ru", "TEST2");

    filmStorage.addLike(film.getId(), userOne.getId());
    filmStorage.addLike(film.getId(), userTwo.getId());

    assertThat(filmStorage.getFilmById(film.getId()).getLikeIds()).hasSize(2);
  }
}
