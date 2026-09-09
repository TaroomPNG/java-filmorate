package ru.yandex.practicum.filmorate;

import java.time.LocalDate;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import ru.yandex.practicum.filmorate.controller.service.storage.DirectorDbStorage;
import ru.yandex.practicum.filmorate.controller.service.FeedService;
import ru.yandex.practicum.filmorate.controller.service.FilmService;
import ru.yandex.practicum.filmorate.controller.service.storage.FeedDbStorage;
import ru.yandex.practicum.filmorate.controller.service.storage.FilmDbStorage;
import ru.yandex.practicum.filmorate.controller.service.storage.ReviewDbStorage;
import ru.yandex.practicum.filmorate.controller.service.storage.UserDbStorage;
import ru.yandex.practicum.filmorate.controller.service.storage.mapper.DirectorRowMapper;
import ru.yandex.practicum.filmorate.controller.service.storage.mapper.FeedRowMapper;
import ru.yandex.practicum.filmorate.controller.service.storage.mapper.FilmRowMapper;
import ru.yandex.practicum.filmorate.controller.service.storage.mapper.ReviewRowMapper;
import ru.yandex.practicum.filmorate.controller.service.storage.mapper.UserRowMapper;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.dto.directorDto.DirectorPostRequest;
import ru.yandex.practicum.filmorate.model.dto.feedDto.FeedPostRequest;
import ru.yandex.practicum.filmorate.model.dto.filmDto.FilmPostRequest;
import ru.yandex.practicum.filmorate.model.dto.reviewDto.ReviewPostRequest;
import ru.yandex.practicum.filmorate.model.dto.userDto.UserPostRequest;

@JdbcTest
@AutoConfigureTestDatabase
@Import({
  UserDbStorage.class,
  FilmDbStorage.class,
  ReviewDbStorage.class,
  DirectorDbStorage.class,
  FeedDbStorage.class,
  UserRowMapper.class,
  FilmRowMapper.class,
  ReviewRowMapper.class,
  DirectorRowMapper.class,
  FeedRowMapper.class,
  FilmService.class,
  FeedService.class
})
@TestPropertySource(
    properties = {
      "spring.sql.init.mode=always",
      "spring.jpa.defer-datasource-initialization=false"
    })
public abstract class DaoTest {
  protected static final LocalDate DATE = LocalDate.of(2000, 12, 12);

  @Autowired protected UserDbStorage userStorage;
  @Autowired protected FilmDbStorage filmStorage;
  @Autowired protected FeedDbStorage feedStorage;
  @Autowired protected ReviewDbStorage reviewStorage;
  @Autowired protected DirectorDbStorage directorStorage;

  @BeforeEach
  void resetData() {
    feedStorage.clear();
    reviewStorage.clear();
    filmStorage.clear();
    directorStorage.clear();
    userStorage.clear();
  }

  protected Long addFeed(Long userId, EventType type, Operation operation, Long entityId) {
    return feedStorage.addFeed(new FeedPostRequest(userId, type, operation, entityId));
  }

  protected User addUser(String email, String login) {
    return userStorage.addUser(
        UserPostRequest.builder().email(email).login(login).birthday(DATE).name(login).build());
  }

  protected Film addFilm(String name) {
    return addFilm(name, Set.of(Genre.DRAMA, Genre.COMEDY), Rating.PG);
  }

  protected Film addFilm(String name, Set<Genre> genres, Rating mpa) {
    return addFilm(name, genres, mpa, DATE);
  }

  protected Film addFilm(String name, Set<Genre> genres, Rating mpa, LocalDate releaseDate) {
    return filmStorage.addFilm(
        FilmPostRequest.builder()
            .name(name)
            .releaseDate(releaseDate)
            .duration(120)
            .genres(genres)
            .mpa(mpa)
            .build());
  }

  protected Director addDirector(String name) {
    DirectorPostRequest request = new DirectorPostRequest();
    request.setName(name);
    return directorStorage.addDirector(request);
  }

  protected Review addReview(String content, Boolean isPositive, Long userId, Long filmId) {
    return reviewStorage.addReview(
        ReviewPostRequest.builder()
            .content(content)
            .isPositive(isPositive)
            .userId(userId)
            .filmId(filmId)
            .build());
  }
}
