package ru.yandex.practicum.filmorate.users;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.DaoTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.dto.filmDto.FilmResponse;

import java.util.List;

class UserGetRecommendations extends DaoTest {

    @Test
    void getOneRecommendedFilm() {
        Film film1 = addFilm("1");
        Film film2 = addFilm("2");
        User user1 = addUser("email@ru","login");
        User user2 = addUser("email2@ru","login2");

        filmStorage.addLike(film1.getId(),user1.getId());
        filmStorage.addLike(film2.getId(),user1.getId());
        filmStorage.addLike(film1.getId(),user2.getId());
        Assertions.assertEquals(List.of(new FilmResponse(filmStorage.getFilmById(film2.getId()))),
                userService.getRecommendations(user2.getId()));
    }

    @Test
    void getSomeRecommendedFilms() {
        Film film1 = addFilm("1");
        Film film2 = addFilm("2");
        Film film3 = addFilm("3");
        User user1 = addUser("email@ru","login");
        User user2 = addUser("email2@ru","login2");

        filmStorage.addLike(film1.getId(),user1.getId());
        filmStorage.addLike(film2.getId(),user1.getId());
        filmStorage.addLike(film3.getId(),user1.getId());
        filmStorage.addLike(film1.getId(),user2.getId());
        Assertions.assertEquals(List.of(new FilmResponse(filmStorage.getFilmById(film2.getId())),
                        new FilmResponse(filmStorage.getFilmById(film3.getId()))),
                userService.getRecommendations(user2.getId()));
    }

    @Test
    void getZeroRecommendedFilms() {
        Film film1 = addFilm("1");
        User user1 = addUser("email@ru","login");
        User user2 = addUser("email2@ru","login2");

        filmStorage.addLike(film1.getId(),user1.getId());
        filmStorage.addLike(film1.getId(),user2.getId());
        Assertions.assertEquals(List.of(),
                userService.getRecommendations(user2.getId()));
    }
}
