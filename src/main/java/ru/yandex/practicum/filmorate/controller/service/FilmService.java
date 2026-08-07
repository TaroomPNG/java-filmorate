package ru.yandex.practicum.filmorate.controller.service;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controller.exceptions.FilmNotFound;
import ru.yandex.practicum.filmorate.controller.exceptions.UserNotFound;
import ru.yandex.practicum.filmorate.controller.service.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.controller.service.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.dto.filmDto.FilmPostRequest;
import ru.yandex.practicum.filmorate.model.dto.filmDto.FilmPutRequest;
import ru.yandex.practicum.filmorate.model.dto.filmDto.FilmResponse;

@Slf4j
@Service
@AllArgsConstructor
public class FilmService {
  @Autowired public final InMemoryFilmStorage filmStorage;
  @Autowired public final InMemoryUserStorage userStorage;

  public FilmResponse deleteLikeOnFilm(Long filmId, Long userId) {
    log.trace("Вызывается deleteLikeOnFilm: FilmID {} - UserID {}", filmId, userId);

    if (!filmStorage.isFilmExists(filmId)) {
      throw new FilmNotFound(filmId);
    }

    if (!userStorage.isUserExists(userId)) {
      throw new UserNotFound(userId);
    }

    Film film = filmStorage.getFilmById(filmId);
    User user = userStorage.getUserById(userId);

    log.debug("Найдено два объекта для удаления лайка: \n Film - {} \n User - {}", film, user);

    if (!film.isLikeExists(user)) {
      log.debug("Лайка на фильме - {} изначально не было возвращаем Response", film);
      return new FilmResponse(film);
    }

    film.deleteLike(user);

    log.debug("Лайк успешно удален");
    log.info("Обновленный объект после удаления - {}", film);

    return new FilmResponse(film);
  }

  public FilmResponse addLikeOnFilm(Long filmId, Long userId) {
    log.trace("Вызывается addLikeOnFilm: FilmID {} -  UserID {}", filmId, userId);
    if (!filmStorage.isFilmExists(filmId)) {
      throw new FilmNotFound(filmId);
    }
    if (!userStorage.isUserExists(userId)) {
      throw new UserNotFound(userId);
    }

    Film film = filmStorage.getFilmById(filmId);
    User user = userStorage.getUserById(userId);

    log.debug("Найдено два объекта для лайка: \n Film - {} \n User - {}", film, user);

    film.addLike(user);

    log.debug("Лайк успешно добавлен");
    log.info("Обновленный объект после добавления - {}", film);

    return new FilmResponse(film);
  }

  public List<FilmResponse> getTopFilms(int maxPosts) {
    log.trace("Запрос getTopFilms");
    log.debug("Значение maxPosts = {}", maxPosts);

    return filmStorage.getFilms().stream()
        .sorted(Comparator.comparing(Film::getLikeCount))
        .limit(maxPosts)
        .map(FilmResponse::new)
        .toList();
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
    return new FilmResponse(filmStorage.addFilm(filmPostRequest));
  }

  public FilmResponse updateFilm(FilmPutRequest filmPutRequest) {
    log.trace("Запрос FilmResponse через updateFilm");
    return new FilmResponse(filmStorage.updateFilm(filmPutRequest));
  }

  public boolean deleteFilm(Long id) {
    log.trace("Запрос FilmResponse через deleteFilm");
    return filmStorage.deleteFilm(id);
  }
}
