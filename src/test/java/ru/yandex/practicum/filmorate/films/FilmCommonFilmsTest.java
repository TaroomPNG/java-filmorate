package ru.yandex.practicum.filmorate.films;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.filmorate.DaoTest;
import ru.yandex.practicum.filmorate.controller.exceptions.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.controller.exceptions.UserNotFound;
import ru.yandex.practicum.filmorate.controller.service.FilmService;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.dto.filmDto.FilmResponse;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public class FilmCommonFilmsTest extends DaoTest {
    @Autowired
    private FilmService filmService;

    private Long user1Id;
    private Long user2Id;
    private Long user3Id;

    @BeforeEach
    void setUp() {
        User user1 = addUser("userTestov1@yandex.ru", "testUser1");
        User user2 = addUser("userTestov2@yandex.ru", "testUser2");
        User user3 = addUser("userTestov3@yandex.ru", "testUser3");

        user1Id = user1.getId();
        user2Id = user2.getId();
        user3Id = user3.getId();

        Film film1 = addFilm("Человек паук: От колобка домой", Set.of(Genre.ACTION_MOVIE, Genre.COMEDY),
                Rating.PG_13);
        Film film2 = addFilm("Начало", Set.of(Genre.ACTION_MOVIE, Genre.THRILLER), Rating.PG_13);
        Film film3 = addFilm("Путешествие сквозь вселенную", Set.of(Genre.DOCUMENTARY), Rating.PG);

        filmStorage.addLike(film1.getId(), user1Id);
        filmStorage.addLike(film2.getId(), user2Id);
        filmStorage.addLike(film3.getId(), user3Id);
        filmStorage.addLike(film1.getId(), user2Id);
    }

    @Test
    void getCommonFilms_WhenNoCommonFilms() {
        List<FilmResponse> result = filmService.getCommonFilms(user1Id, user3Id);
        assertThat(result).isEmpty();
    }

    @Test
    void getCommonFilms_ShouldReturnCommonFilms() {
        List<FilmResponse> result = filmService.getCommonFilms(user1Id, user2Id);
        assertThat(result).hasSize(1);
    }

    @Test
    void getCommonFilms_ShouldThrowException_WhenSameUser() {

        assertThatThrownBy(() -> filmService.getCommonFilms(user1Id, user1Id))
                .isInstanceOf(ConditionsNotMetException.class);
    }

    @Test
    void getCommonFilms_ShouldThrowException_WhenUserIdIsZero() {
        assertThatThrownBy(() -> filmService.getCommonFilms(0L, user1Id))
                .isInstanceOf(ConditionsNotMetException.class);

        assertThatThrownBy(() -> filmService.getCommonFilms(user1Id, 0L))
                .isInstanceOf(ConditionsNotMetException.class);
    }

    @Test
    void getCommonFilms_ShouldThrowException_WhenUserIdIsNegative() {
        assertThatThrownBy(() -> filmService.getCommonFilms(-1L, user1Id))
                .isInstanceOf(ConditionsNotMetException.class);

        assertThatThrownBy(() -> filmService.getCommonFilms(user1Id, -1L))
                .isInstanceOf(ConditionsNotMetException.class);
    }

    @Test
    void getCommonFilms_ShouldThrowException_WhenUserNotFound() {
        assertThatThrownBy(() -> filmService.getCommonFilms(100500L, user1Id))
                .isInstanceOf(UserNotFound.class);
    }
}