package ru.yandex.practicum.filmorate.films;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.DaoTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

class FilmPopularGetTest extends DaoTest {

  @Test
  void getPopularFilmsOrderedByLikesDescending() {
    User userOne = addUser("test@yandex.ru", "TEST");
    User userTwo = addUser("test2@yandex.ru", "TEST2");
    Film twoLikes = addFilm("FILM_TWO_LIKES");
    Film oneLike = addFilm("FILM_ONE_LIKE");
    Film noLikes = addFilm("FILM_NO_LIKES");

    filmStorage.addLike(twoLikes.getId(), userOne.getId());
    filmStorage.addLike(twoLikes.getId(), userTwo.getId());
    filmStorage.addLike(oneLike.getId(), userOne.getId());

    List<Film> popular = filmStorage.getPopular(10);

    assertThat(popular)
        .extracting(Film::getId)
        .containsExactly(twoLikes.getId(), oneLike.getId(), noLikes.getId());
  }

  @Test
  void getPopularFilmsWithCountLimit() {
    User userOne = addUser("test@yandex.ru", "TEST");
    User userTwo = addUser("test2@yandex.ru", "TEST2");
    Film twoLikes = addFilm("FILM_TWO_LIKES");
    addFilm("FILM_ONE_LIKE");
    addFilm("FILM_NO_LIKES");

    filmStorage.addLike(twoLikes.getId(), userOne.getId());
    filmStorage.addLike(twoLikes.getId(), userTwo.getId());

    assertThat(filmStorage.getPopular(1)).extracting(Film::getId).containsExactly(twoLikes.getId());
  }

  @Test
  void getPopularFilmsWithCountLargerThanAvailable() {
    addFilm("FILM1");
    addFilm("FILM2");
    addFilm("FILM3");

    assertThat(filmStorage.getPopular(50)).hasSize(3);
  }
}
