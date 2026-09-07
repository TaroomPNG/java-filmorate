package ru.yandex.practicum.filmorate.controller.service.storage;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.controller.exceptions.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.controller.exceptions.FilmNotFound;
import ru.yandex.practicum.filmorate.controller.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.controller.exceptions.UserNotFound;
import ru.yandex.practicum.filmorate.controller.service.storage.mapper.FilmRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.dto.filmDto.FilmPostRequest;
import ru.yandex.practicum.filmorate.model.dto.filmDto.FilmPutRequest;

@Repository("FilmDbStorage")
public class FilmDbStorage extends BaseRepository<Film> implements FilmStorage {

  private static final String FILM_SELECT =
      "SELECT f.film_id, f.name, f.description, f.release_date, f.duration, r.rating_id, r.rating "
          + "FROM film AS f JOIN rating AS r ON f.rating_id = r.rating_id";
  private static final String FIND_BY_ID_FILM = FILM_SELECT + " WHERE f.film_id = ?";
  private static final String FIND_ALL_FILMS = FILM_SELECT + " ORDER BY f.film_id";
  private static final String FIND_POPULAR =
      FILM_SELECT
          + " LEFT JOIN film_likes AS fl ON f.film_id = fl.film_id "
          + "GROUP BY f.film_id, f.name, f.description, f.release_date, f.duration, r.rating_id, r.rating "
          + "ORDER BY COUNT(fl.user_id) DESC, f.film_id LIMIT ?";
  private static final String DELETE_FILM_GENRES = "DELETE FROM film_to_genres WHERE film_id = ?";
  private static final String DELETE_FILM_LIKES = "DELETE FROM film_likes WHERE film_id = ?";
  private static final String DELETE_BY_ID_FILM = "DELETE FROM film WHERE film_id = ?";
  private static final String ADD_NEW_FILM =
      "INSERT INTO film (name, description, release_date, duration, rating_id) "
          + "VALUES (?, ?, ?, ?, ?)";
  private static final String ADD_FILM_TO_GENRES =
      "INSERT INTO film_to_genres (film_id, genre_id) VALUES (?, ?)";
  private static final String FIND_GENRES_BY_FILM =
      "SELECT g.genre_id, g.genre FROM genres AS g "
          + "JOIN film_to_genres AS fg ON g.genre_id = fg.genre_id "
          + "WHERE fg.film_id = ? ORDER BY g.genre_id";
  private static final String FIND_ALL_GENRES =
      "SELECT genre_id, genre FROM genres ORDER BY genre_id";
  private static final String FIND_BY_ID_GENRE =
      "SELECT genre_id, genre FROM genres WHERE genre_id = ?";
  private static final String FIND_ALL_RATINGS =
      "SELECT rating_id, rating FROM rating ORDER BY rating_id";
  private static final String FIND_BY_ID_RATING =
      "SELECT rating_id, rating FROM rating WHERE rating_id = ?";
  private static final String ADD_LIKE =
      "MERGE INTO film_likes (user_id, film_id) KEY (user_id, film_id) VALUES (?, ?)";
  private static final String DELETE_LIKE =
      "DELETE FROM film_likes WHERE film_id = ? AND user_id = ?";

  private static final String FIND_LIKES_BY_FILM =
      "SELECT u.* FROM \"user\" AS u "
          + "JOIN film_likes AS fl ON u.user_id = fl.user_id WHERE fl.film_id = ?";

  private static final String SEARCH_FILMS =
      "SELECT f.*, r.rating FROM film AS f "
          + "LEFT JOIN rating AS r ON f.rating_id = r.rating_id "
          + "WHERE LOWER(f.name) LIKE LOWER(?) "
          + "OR LOWER(f.description) LIKE LOWER(?)";

  private static final String FIND_COMMON_FILMS =
      "SELECT f.* FROM film AS f "
          + "INNER JOIN film_likes AS fl1 ON f.film_id = fl1.film_id "
          + "INNER JOIN film_likes AS fl2 ON f.film_id = fl2.film_id "
          + "WHERE fl1.user_id = ? AND fl2.user_id = ?";

  private final RowMapper<Genre> genreMapper =
      (rs, rowNum) -> new Genre(rs.getInt("genre_id"), rs.getString("genre"));
  private final RowMapper<Rating> ratingMapper =
      (rs, rowNum) -> new Rating(rs.getInt("rating_id"), rs.getString("rating"));
  private final RowMapper<User> userMapper;

  private UserStorage userStorage;

  public FilmDbStorage(
      JdbcTemplate jdbc,
      @Qualifier("filmRowMapper") RowMapper<Film> mapper,
      @Qualifier("userRowMapper") RowMapper<User> userMapper,
      @Qualifier("userDbStorage") UserStorage userStorage) {
    super(jdbc, mapper);
    this.userMapper = userMapper;
    this.userStorage = userStorage;
  }

  @Override
  public Film addFilm(FilmPostRequest filmPostRequest) {
    String description = filmPostRequest.getDescription();
    if (description == null || description.isBlank()) {
      description = "-";
    }

    long id =
        insert(
            ADD_NEW_FILM,
            filmPostRequest.getName(),
            description,
            filmPostRequest.getReleaseDate(),
            filmPostRequest.getDuration(),
            filmPostRequest.getMpa().getId());

    saveGenres(id, filmPostRequest.getGenres());
    return getFilmById(id);
  }

  @Override
  public Film updateFilm(FilmPutRequest filmPutRequest) {
    if (!isFilmExists(filmPutRequest.getId())) {
      throw new FilmNotFound(filmPutRequest.getId());
    }

    List<String> setClauses = new ArrayList<>();
    List<Object> params = new ArrayList<>();

    if (filmPutRequest.getName() != null) {
      setClauses.add("name = ?");
      params.add(filmPutRequest.getName());
    }
    if (filmPutRequest.getDescription() != null) {
      setClauses.add("description = ?");
      params.add(filmPutRequest.getDescription());
    }
    if (filmPutRequest.getReleaseDate() != null) {
      setClauses.add("release_date = ?");
      params.add(filmPutRequest.getReleaseDate());
    }
    if (filmPutRequest.getDuration() != null) {
      setClauses.add("duration = ?");
      params.add(filmPutRequest.getDuration());
    }
    if (filmPutRequest.getMpa() != null) {
      setClauses.add("rating_id = ?");
      params.add(filmPutRequest.getMpa().getId());
    }

    if (!setClauses.isEmpty()) {
      String updateQuery =
          "UPDATE film SET " + String.join(", ", setClauses) + " WHERE film_id = ?";
      params.add(filmPutRequest.getId());
      update(updateQuery, params.toArray());
    }

    if (filmPutRequest.getGenres() != null) {
      jdbc.update(DELETE_FILM_GENRES, filmPutRequest.getId());
      saveGenres(filmPutRequest.getId(), filmPutRequest.getGenres());
    }

    return getFilmById(filmPutRequest.getId());
  }

  @Override
  public boolean deleteFilm(long id) {
    jdbc.update(DELETE_FILM_GENRES, id);
    jdbc.update(DELETE_FILM_LIKES, id);
    return delete(DELETE_BY_ID_FILM, id);
  }

  @Override
  public Film getFilmById(long id) {
    Optional<Film> optionalFilm = findOne(FIND_BY_ID_FILM, id);
    if (optionalFilm.isEmpty()) {
      throw new FilmNotFound(id);
    }
    Film film = optionalFilm.get();
    film.setGenres(getFilmGenres(film.getId()));
    film.getLikeIds().addAll(jdbc.query(FIND_LIKES_BY_FILM, userMapper, film.getId()));
    return film;
  }

  @Override
  public List<Film> getFilms() {
    List<Film> films = List.copyOf(findMany(FIND_ALL_FILMS));
    films.forEach(film -> film.setGenres(getFilmGenres(film.getId())));
    return films;
  }

  @Override
  public boolean isFilmExists(long id) {
    return findOne(FIND_BY_ID_FILM, id).isPresent();
  }

  @Override
  public Set<Genre> getFilmGenres(long id) {
    return new LinkedHashSet<>(jdbc.query(FIND_GENRES_BY_FILM, genreMapper, id));
  }

  @Override
  public List<Genre> getGenres() {
    return jdbc.query(FIND_ALL_GENRES, genreMapper);
  }

  @Override
  public Genre getGenreById(long id) {
    try {
      return jdbc.queryForObject(FIND_BY_ID_GENRE, genreMapper, id);
    } catch (EmptyResultDataAccessException e) {
      throw new NotFoundException("Жанр с id " + id + " не найден");
    }
  }

  @Override
  public List<Rating> getRatings() {
    return jdbc.query(FIND_ALL_RATINGS, ratingMapper);
  }

  @Override
  public Rating getRatingById(long id) {
    try {
      return jdbc.queryForObject(FIND_BY_ID_RATING, ratingMapper, id);
    } catch (EmptyResultDataAccessException e) {
      throw new NotFoundException("Рейтинг с id " + id + " не найден");
    }
  }

  @Override
  public boolean isGenreExists(long id) {
    try {
      getGenreById(id);
      return true;
    } catch (NotFoundException e) {
      return false;
    }
  }

  @Override
  public boolean isRatingExists(long id) {
    try {
      getRatingById(id);
      return true;
    } catch (NotFoundException e) {
      return false;
    }
  }

  @Override
  public void addLike(long filmId, long userId) {
    jdbc.update(ADD_LIKE, userId, filmId);
  }

  @Override
  public void removeLike(long filmId, long userId) {
    jdbc.update(DELETE_LIKE, filmId, userId);
  }

  @Override
  public List<Film> getPopular(int count) {
    List<Film> films = List.copyOf(findMany(FIND_POPULAR, count));
    films.forEach(film -> film.setGenres(getFilmGenres(film.getId())));
    return films;
  }

  @Override
  public void clear() {
    jdbc.update("DELETE FROM film_likes");
    jdbc.update("DELETE FROM film_to_genres");
    jdbc.update("DELETE FROM film");
    jdbc.update("ALTER TABLE film ALTER COLUMN film_id RESTART WITH 1");
  }

  private void saveGenres(long filmId, Set<Genre> genres) {
    if (genres == null || genres.isEmpty()) {
      return;
    }
    Set<Integer> uniqueIds = new LinkedHashSet<>();
    for (Genre genre : genres) {
      uniqueIds.add(genre.getId());
    }
    List<Object[]> params =
        uniqueIds.stream().map(genreId -> new Object[] {filmId, genreId}).toList();
    jdbc.batchUpdate(ADD_FILM_TO_GENRES, params);
  }

//  Добавлен метод для поиска фильм-а/-ов по названию или описанию.
  @Override
  public List<Film> searchFilms(String query) {
    if (query == null || query.isBlank()) {
        throw new ConditionsNotMetException("Поисковые данные не введены");
    }
    String template = "%" + query.trim() + "%";
    List<Film> films = jdbc.query(SEARCH_FILMS, new FilmRowMapper(), template, template);

    films.forEach(film -> film.setGenres(getFilmGenres(film.getId())));
    return films;
  }

  //  Добавлен метод для получения общих фильмов у двух разных пользователей.
  public List<Film> getCommonFilms(long userId1, long userId2) {
      if (userId1 <= 0 || userId2 <= 0) {
            throw new ConditionsNotMetException("ID пользователя должен быть положительным числом");
      }
      if (userId1 == userId2) {
          throw new ConditionsNotMetException("Пользователи должны быть разные");
      }
      if (!userStorage.isUserExists(userId1)) {
          throw new UserNotFound(userId1);
      }
      if (!userStorage.isUserExists(userId2)) {
          throw new UserNotFound(userId2);
      }

      List<Film> films = jdbc.query(FIND_COMMON_FILMS, new FilmRowMapper(), userId1, userId2);

      films.forEach(film -> film.setGenres(getFilmGenres(film.getId())));
      return films;
    }
}
