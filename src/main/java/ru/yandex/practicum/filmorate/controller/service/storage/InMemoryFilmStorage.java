package ru.yandex.practicum.filmorate.controller.service.storage;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.controller.exceptions.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.controller.exceptions.FilmNotFound;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.dto.filmDto.FilmPostRequest;
import ru.yandex.practicum.filmorate.model.dto.filmDto.FilmPutRequest;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
  private final Map<Long, Film> filmHashMap = new HashMap<>();

  private static void updateFilmDuration(FilmPutRequest filmPutRequest, Film oldFilm) {
    log.trace("Вызывается метод updateFilmDuration");
    oldFilm.setDuration(filmPutRequest.getDuration());
    log.debug("Фильм успешно обновил длительность на: {}", oldFilm.getDuration());
  }

  private static void updateReleaseDate(FilmPutRequest filmPutRequest, Film oldFilm) {
    log.trace("Вызывается метод updateReleaseDate");
    LocalDate earliestDate = LocalDate.of(1895, 12, 28);

    if (filmPutRequest.getReleaseDate().isBefore(earliestDate)) {
      log.warn(
          "Ошибка валидации: дата {} раньше минимально допустимой {}",
          filmPutRequest.getReleaseDate(),
          earliestDate);
      throw new ConditionsNotMetException("Дата обновления некорректна (слишком старая)");
    }

    oldFilm.setReleaseDate(filmPutRequest.getReleaseDate());
    log.debug("Фильм успешно обновил дату на: {}", oldFilm.getReleaseDate());
  }

  private static void updateFilmDescription(FilmPutRequest filmPutRequest, Film oldFilm) {
    log.trace("Вызывается метод updateFilmDescription");
    oldFilm.setDescription(filmPutRequest.getDescription());
    log.debug("Фильм успешно обновил описание на: {}", oldFilm.getDescription());
  }

  public boolean isFilmExists(long id) {
    log.trace("Запрос isFilmExists");
    return filmHashMap.containsKey(id);
  }

  public void clearMap() {
    log.trace("Запрос на очистку filmHashMap");
    filmHashMap.clear();
  }

  @Override
  public Film addFilm(FilmPostRequest filmPostRequest) {
    log.debug("Запрос addFilm: {}", filmPostRequest);
    LocalDate earliestDate = LocalDate.of(1895, 12, 28);

    if (filmPostRequest.getReleaseDate().isBefore(earliestDate)) {
      log.warn(
          "Ошибка валидации: дата {} раньше минимально допустимой {}",
          filmPostRequest.getReleaseDate(),
          earliestDate);
      throw new ConditionsNotMetException(
          String.format(
              "Ошибка: дата фильма %s раньше минимально допустимой %s",
              filmPostRequest.getReleaseDate(), earliestDate));
    }

    boolean existFilmName =
        filmHashMap.values().stream()
            .anyMatch(film -> film.getName().equalsIgnoreCase(filmPostRequest.getName()));

    if (existFilmName) {
      log.warn("Ошибка валидации: фильм с именем {} уже добавлен", filmPostRequest.getName());
      throw new ConditionsNotMetException(
          String.format("Фильм с именем %s уже добавлен", filmPostRequest.getName()));
    }

    Film newFilm =
        new Film(
            generateId(),
            filmPostRequest.getName(),
            filmPostRequest.getDescription(),
            filmPostRequest.getReleaseDate(),
            filmPostRequest.getDuration(),
            filmPostRequest.getGenres(),
            filmPostRequest.getRating());

    log.debug("Сформирован объект {}", newFilm);

    if (filmPostRequest.getDescription() == null || filmPostRequest.getDescription().isBlank()) {
      log.debug("Поступил пустой Description - устанавливается стандартное значение <->");
      newFilm.setDescription("-");
    }

    filmHashMap.put(newFilm.getId(), newFilm);

    log.info("Объект {} успешно добавлен по ID - {}", newFilm.getName(), newFilm.getId());

    return newFilm;
  }

  @Override
  public Film updateFilm(FilmPutRequest filmPutRequest) {
    log.trace("Вызывается updateFilm");
    Film oldFilm = filmHashMap.get(filmPutRequest.getId());

    if (oldFilm == null) {
      log.warn("Ошибка валидации: фильм по ID - {} не найден", filmPutRequest.getId());
      throw new FilmNotFound(filmPutRequest.getId());
    }

    if (filmPutRequest.getName() != null) {
      log.debug("Обнаружено поле Name");
      updateFilmName(filmPutRequest, oldFilm);
    }
    if (filmPutRequest.getDescription() != null) {
      log.debug("Обнаружено поле Description");
      updateFilmDescription(filmPutRequest, oldFilm);
    }
    if (filmPutRequest.getReleaseDate() != null) {
      log.debug("Обнаружено поле ReleaseDate");
      updateReleaseDate(filmPutRequest, oldFilm);
    }
    if (filmPutRequest.getDuration() != null) {
      log.debug("Обнаружено поле Duration");
      updateFilmDuration(filmPutRequest, oldFilm);
    }

    log.info("Обновленный объект Film - {}", oldFilm);

    return oldFilm;
  }

  @Override
  public boolean deleteFilm(long id) {
    log.trace("Запрос deleteFilm");
    if (!filmHashMap.containsKey(id)) {
      log.warn("Ошибка валидации: фильм по ID - {} не найден", id);
      throw new FilmNotFound(id);
    }
    filmHashMap.remove(id);
    log.debug("Фильм по ID {} успешно удален", id);
    return true;
  }

  @Override
  public Film getFilmById(long id) {
    log.trace("Запрос getFilmById");

    if (!filmHashMap.containsKey(id)) {
      log.warn("Ошибка валидации: фильм по ID - {} не найден", id);
      throw new FilmNotFound(id);
    }
    return filmHashMap.get(id);
  }

  @Override
  public List<Film> getFilms() {
    log.trace("Запрос getAllFilms");

    if (filmHashMap.isEmpty()) {
      log.warn("Ошибка валидации: на сервере отсутствуют фильмы");
      throw new FilmNotFound("На сервере отсутствуют фильмы // Фильмов для передачи нет");
    }

    return filmHashMap.values().stream().toList();
  }

  private Long generateId() {
    long currentMaxId = filmHashMap.values().stream().mapToLong(Film::getId).max().orElse(0);

    log.info("Сгенерирован ID - {}", currentMaxId);
    return ++currentMaxId;
  }

  private void updateFilmName(FilmPutRequest filmPutRequest, Film oldFilm) {
    log.trace("Вызывается метод updateFilmName");
    boolean existFilmName =
        filmHashMap.values().stream()
            .anyMatch(film -> film.getName().equalsIgnoreCase(filmPutRequest.getName()));

    if (existFilmName) {
      log.warn("Ошибка валидации: имя фильма {} занято для обновления", filmPutRequest.getName());
      throw new ConditionsNotMetException(
          String.format("Имя фильма %s занято для обновления", filmPutRequest.getName()));
    }
    oldFilm.setName(filmPutRequest.getName());
    log.debug("Фильм успешно обновил имя на: {}", oldFilm.getName());
  }
}
