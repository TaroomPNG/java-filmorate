package ru.yandex.practicum.filmorate.films;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.DaoTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
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

  @Test
  @DisplayName("Популярные фильмы указанного жанра, отсортированные по лайкам")
  void getPopularFilmsFilteredByGenre() {
    User userOne = addUser("test@yandex.ru", "TEST");
    User userTwo = addUser("test2@yandex.ru", "TEST2");
    Film twoLikes = addFilm("Целлулоидный шкаф", Set.of(Genre.DOCUMENTARY), Rating.PG);
    Film oneLike = addFilm("Туман войны", Set.of(Genre.DOCUMENTARY), Rating.PG);
    Film noLikes = addFilm("Автобус 174", Set.of(Genre.DOCUMENTARY), Rating.PG);
    addFilm("OTHER_GENRE", Set.of(Genre.THRILLER), Rating.PG);

    filmStorage.addLike(twoLikes.getId(), userOne.getId());
    filmStorage.addLike(twoLikes.getId(), userTwo.getId());
    filmStorage.addLike(oneLike.getId(), userOne.getId());

    List<Film> popular = filmStorage.getPopular(10, Genre.DOCUMENTARY.getId(), null);

    assertThat(popular)
        .extracting(Film::getId)
        .containsExactly(twoLikes.getId(), oneLike.getId(), noLikes.getId());
  }

  @Test
  @DisplayName("Популярные фильмы указанного года, отсортированные по лайкам")
  void getPopularFilmsFilteredByYear() {
    User userOne = addUser("test@yandex.ru", "TEST");
    User userTwo = addUser("test2@yandex.ru", "TEST2");
    Film twoLikes = addFilm("1+1", Set.of(Genre.DRAMA), Rating.PG, LocalDate.of(1999, 2, 1));
    Film oneLike = addFilm("Зеленая миля", Set.of(Genre.DRAMA), Rating.PG, LocalDate.of(1999, 2, 10));
    Film noLikes = addFilm("Коммерсант", Set.of(Genre.DRAMA), Rating.PG, LocalDate.of(1999, 5, 5));
    addFilm("Достучаться до небес", Set.of(Genre.DRAMA), Rating.PG, LocalDate.of(2001, 1, 1));

    filmStorage.addLike(twoLikes.getId(), userOne.getId());
    filmStorage.addLike(twoLikes.getId(), userTwo.getId());
    filmStorage.addLike(oneLike.getId(), userOne.getId());

    List<Film> popular = filmStorage.getPopular(10, null, 1999);

    //popular должен быть в порядке 2 лайка, один лайк, без лайков
    assertThat(popular)
        .extracting(Film::getId)
        .containsExactly(twoLikes.getId(), oneLike.getId(), noLikes.getId());
  }

  @Test
  @DisplayName("Популярные фильмы по жанру и году")
  void getPopularFilmsFilteredByGenreAndYear() {
    User userOne = addUser("test@yandex.ru", "TEST");
    Film match = addFilm("Париж горит", Set.of(Genre.DOCUMENTARY), Rating.PG, LocalDate.of(2001, 7, 8));
    addFilm("Крамб", Set.of(Genre.DOCUMENTARY), Rating.PG, LocalDate.of(1999, 1, 1));
    addFilm("Кошмар на улице Вязов", Set.of(Genre.THRILLER), Rating.PG, LocalDate.of(2001, 7, 8));

    filmStorage.addLike(match.getId(), userOne.getId());

    List<Film> popular = filmStorage.getPopular(10, Genre.DOCUMENTARY.getId(), 2001);

    //остается только один документальный фильм 2010 года
    assertThat(popular).extracting(Film::getId).containsExactly(match.getId());
  }

  @Test
  @DisplayName("Фильм с несколькими жанрами в выборке по одному из них")
  void getPopularFilmsIncludesFilmWithMultipleGenres() {
    Film multi = addFilm("Залечь на дно в Брюгге", Set.of(Genre.COMEDY, Genre.DOCUMENTARY), Rating.PG);
    addFilm("Остров проклятых", Set.of(Genre.THRILLER), Rating.PG);

    List<Film> popular = filmStorage.getPopular(10, Genre.DOCUMENTARY.getId(), null);
    //в выборке остается только документальный фильм
    assertThat(popular).extracting(Film::getId).containsExactly(multi.getId());
  }
}
