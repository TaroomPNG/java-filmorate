package ru.yandex.practicum.filmorate.controller.service;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controller.exceptions.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.controller.exceptions.FilmNotFound;
import ru.yandex.practicum.filmorate.controller.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.controller.exceptions.UserNotFound;
import ru.yandex.practicum.filmorate.controller.service.storage.FilmStorage;
import ru.yandex.practicum.filmorate.controller.service.storage.ReviewStorage;
import ru.yandex.practicum.filmorate.controller.service.storage.UserStorage;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.dto.feedDto.FeedPostRequest;
import ru.yandex.practicum.filmorate.model.dto.filmDto.FilmPostRequest;
import ru.yandex.practicum.filmorate.model.dto.filmDto.FilmPutRequest;
import ru.yandex.practicum.filmorate.model.dto.filmDto.FilmResponse;
import ru.yandex.practicum.filmorate.model.dto.genreDto.GenreResponse;

@Slf4j
@Service
public class FilmService {
  private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);
  private static final Set<String> ALLOWED_SEARCH_BY = Set.of("title", "director");

  @Autowired
  @Qualifier("FilmDbStorage")
  FilmStorage filmStorage;

  @Autowired
  @Qualifier("UserDbStorage")
  UserStorage userStorage;

  @Autowired
  @Qualifier("ReviewDbStorage")
  ReviewStorage reviewStorage;

  @Autowired private FeedService feedService;

  public FilmResponse deleteLikeOnFilm(Long filmId, Long userId) {
    log.trace("Вызывается deleteLikeOnFilm: FilmID {} - UserID {}", filmId, userId);
    if (!filmStorage.isFilmExists(filmId)) {
      throw new FilmNotFound(filmId);
    }
    if (!userStorage.isUserExists(userId)) {
      throw new UserNotFound(userId);
    }
    filmStorage.removeLike(filmId, userId);
    feedService.addToFeed(new FeedPostRequest(userId, EventType.LIKE, Operation.REMOVE, filmId));

    return new FilmResponse(filmStorage.getFilmById(filmId));
  }

  public FilmResponse addLikeOnFilm(Long filmId, Long userId) {
    log.trace("Вызывается addLikeOnFilm: FilmID {} -  UserID {}", filmId, userId);
    if (!filmStorage.isFilmExists(filmId)) {
      throw new FilmNotFound(filmId);
    }
    if (!userStorage.isUserExists(userId)) {
      throw new UserNotFound(userId);
    }
    filmStorage.addLike(filmId, userId);
    feedService.addToFeed(new FeedPostRequest(userId, EventType.LIKE, Operation.ADD, filmId));

    return new FilmResponse(filmStorage.getFilmById(filmId));
  }

  public List<FilmResponse> getTopFilms(int count, Integer genreId, Integer year) {
    log.trace("Запрос getTopFilms: count {}, genreId {}, year {}", count, genreId, year);
    return filmStorage.getPopular(count, genreId, year).stream().map(FilmResponse::new).toList();
  }

  public Collection<FilmResponse> getFilms() {
    log.trace("Запрос FilmResponse через getFilms");
    return filmStorage.getFilms().stream().map(FilmResponse::new).toList();
  }

  public FilmResponse getFilmById(Long id) {
    log.trace("Запрос FilmResponse через getFilmById");
    return new FilmResponse(filmStorage.getFilmById(id));
  }

  public FilmResponse addFilm(FilmPostRequest filmPostRequest) {
    log.trace("Запрос FilmResponse через addFilm");
    validateReleaseDate(filmPostRequest.getReleaseDate());
    validateMpa(filmPostRequest.getMpa());
    validateGenres(filmPostRequest.getGenres());
    return new FilmResponse(filmStorage.addFilm(filmPostRequest));
  }

  public FilmResponse updateFilm(FilmPutRequest filmPutRequest) {
    log.trace("Запрос FilmResponse через updateFilm");
    if (filmPutRequest.getReleaseDate() != null) {
      validateReleaseDate(filmPutRequest.getReleaseDate());
    }
    if (filmPutRequest.getMpa() != null) {
      validateMpa(filmPutRequest.getMpa());
    }
    if (filmPutRequest.getGenres() != null) {
      validateGenres(filmPutRequest.getGenres());
    }
    return new FilmResponse(filmStorage.updateFilm(filmPutRequest));
  }

  public boolean deleteFilm(Long id) {
    log.trace("Запрос FilmResponse через deleteFilm");
    reviewStorage.deleteByFilmId(id);
    return filmStorage.deleteFilm(id);
  }

  public List<GenreResponse> getGenres() {
    log.trace("Запрос GenreResponse через getGenres");
    return filmStorage.getGenres().stream().map(GenreResponse::new).toList();
  }

  public GenreResponse getGenreById(long id) {
    log.trace("Запрос GenreResponse через getGenreById");
    return new GenreResponse(filmStorage.getGenreById(id));
  }

  public List<Rating> getRatings() {
    return filmStorage.getRatings();
  }

  public Rating getRatingById(long id) {
    return filmStorage.getRatingById(id);
  }

  private void validateReleaseDate(LocalDate releaseDate) {
    if (releaseDate.isBefore(MIN_RELEASE_DATE)) {
      throw new ConditionsNotMetException("Дата релиза не может быть раньше 28 декабря 1895 года");
    }
  }

  private void validateMpa(Rating mpa) {
    if (mpa == null || mpa.getId() == null || !filmStorage.isRatingExists(mpa.getId())) {
      throw new NotFoundException("Указанный рейтинг не найден");
    }
  }

  private void validateGenres(java.util.Set<Genre> genres) {
    if (genres == null) {
      return;
    }
    for (Genre genre : genres) {
      if (genre.getId() == null || !filmStorage.isGenreExists(genre.getId())) {
        throw new NotFoundException("Указанный жанр не найден");
      }
    }
  }

    public List<FilmResponse> searchFilms(String query, String by) {
        if (query == null || query.isBlank()) {
            throw new ConditionsNotMetException("Query не может быть пустым");
        }
        if (by == null || by.isBlank()) {
            throw new ConditionsNotMetException("By не может быть пустым");
        }

        String[] byParams = by.toLowerCase().trim().split(",");

        boolean searchByTitle = false;
        boolean searchByDirector = false;

        for (String patameter : byParams) {
            String correctBy = patameter.trim();

            if (!ALLOWED_SEARCH_BY.contains(correctBy)) {
                throw new ConditionsNotMetException("Некорректное значение by! Должен быть 'title' или 'director'");
            }

            if ("title".equals(correctBy)) {
                searchByTitle = true;
            } else if ("director".equals(correctBy)) {
                searchByDirector = true;
            }
        }

        return filmStorage.searchFilms(query, searchByTitle, searchByDirector)
                .stream()
                .map(FilmResponse::new)
                .toList();
    }
}
