package ru.yandex.practicum.filmorate;

import java.time.LocalDate;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import ru.yandex.practicum.filmorate.controller.service.storage.FilmDbStorage;
import ru.yandex.practicum.filmorate.controller.service.storage.UserDbStorage;
import ru.yandex.practicum.filmorate.controller.service.storage.mapper.FilmRowMapper;
import ru.yandex.practicum.filmorate.controller.service.storage.mapper.UserRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.dto.filmDto.FilmPostRequest;
import ru.yandex.practicum.filmorate.model.dto.userDto.UserPostRequest;

@JdbcTest
@AutoConfigureTestDatabase
@Import({UserDbStorage.class, FilmDbStorage.class, UserRowMapper.class, FilmRowMapper.class})
@TestPropertySource(
    properties = {
      "spring.sql.init.mode=always",
      "spring.jpa.defer-datasource-initialization=false"
    })
public abstract class DaoTest {
  protected static final LocalDate DATE = LocalDate.of(2000, 12, 12);

  @Autowired protected UserDbStorage userStorage;
  @Autowired protected FilmDbStorage filmStorage;

  @BeforeEach
  void resetData() {
    filmStorage.clear();
    userStorage.clear();
  }

  protected User addUser(String email, String login) {
    return userStorage.addUser(
        UserPostRequest.builder().email(email).login(login).birthday(DATE).name(login).build());
  }

  protected Film addFilm(String name) {
    return addFilm(name, Set.of(Genre.DRAMA, Genre.COMEDY), Rating.PG);
  }

  protected Film addFilm(String name, Set<Genre> genres, Rating mpa) {
    return filmStorage.addFilm(
        FilmPostRequest.builder()
            .name(name)
            .releaseDate(DATE)
            .duration(120)
            .genres(genres)
            .mpa(mpa)
            .build());
  }
}
