package ru.yandex.practicum.filmorate.controller.service;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controller.exceptions.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.controller.exceptions.UserNotFound;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.dto.filmDto.FilmPostRequest;
import ru.yandex.practicum.filmorate.model.dto.filmDto.FilmPutRequest;
import ru.yandex.practicum.filmorate.model.dto.filmDto.FilmResponse;

@Slf4j
@Service
public class FilmService {
  private final HashMap<Long, Film> filmHashMap = new HashMap<>();

  private static void updateFilmDuration(FilmPutRequest filmPutRequest, Film oldFilm) {
    log.trace("Вызывается метод updateFilmDuration");
    oldFilm.setDuration(filmPutRequest.getDuration());
    log.trace("Фильм успешно обновил длительность на: {}", oldFilm.getDuration());
  }

  private static void updateReleaseDate(FilmPutRequest filmPutRequest, Film oldFilm) {
    log.trace("Вызывается метод updateReleaseDate");
    LocalDate earliestDate = LocalDate.of(1895, 12, 28);

    if (filmPutRequest.getReleaseDate().isBefore(earliestDate)) {
      log.warn("Ошибка валидации: дата обновления некорректна (слишком старая)");
      throw new ConditionsNotMetException("Некорректная дата фильма");
    }

    oldFilm.setReleaseDate(filmPutRequest.getReleaseDate());
    log.trace("Фильм успешно обновил дату на: {}", oldFilm.getReleaseDate());
  }

  private static void updateFilmDescription(FilmPutRequest filmPutRequest, Film oldFilm) {
    log.trace("Вызывается метод updateFilmDescription");
    oldFilm.setDescription(filmPutRequest.getDescription());
    log.trace("Фильм успешно обновил описание на: {}", oldFilm.getDescription());
  }

  public void clearMap() {
    log.trace("Запрос на очистку filmHashMap");
    filmHashMap.clear();
  }

  public FilmResponse getFilm(Long id) {
    log.trace("Запрос getFilm");

    if (!filmHashMap.containsKey(id)) {
      throw new ConditionsNotMetException("Фильм по объекту не найден");
    }

    Film film = filmHashMap.get(id);

    return FilmResponse.builder()
        .id(id)
        .name(film.getName())
        .description(film.getDescription())
        .releaseDate(film.getReleaseDate())
        .duration(film.getDuration())
        .build();
  }

  public Collection<FilmResponse> getAllFilms() {
    log.trace("Запрос getAllFilms");
    return filmHashMap.values().stream()
        .map(
            film ->
                FilmResponse.builder()
                    .id(film.getId())
                    .name(film.getName())
                    .description(film.getDescription())
                    .releaseDate(film.getReleaseDate())
                    .duration(film.getDuration())
                    .build())
        .collect(Collectors.toList());
  }

  public FilmResponse addFilm(FilmPostRequest filmPostRequest) {
    log.debug("Запрос addFilm: {}", filmPostRequest);
    LocalDate earliestDate = LocalDate.of(1895, 12, 28);

    if (filmPostRequest.getReleaseDate().isBefore(earliestDate)) {
      log.warn(
          "Ошибка валидации: дата фильма {} раньше минимально допустимой {}",
          filmPostRequest.getReleaseDate(),
          earliestDate);
      throw new ConditionsNotMetException("Некорректная дата фильма");
    }

    boolean existFilmName =
        filmHashMap.values().stream()
            .anyMatch(film -> film.getName().equalsIgnoreCase(filmPostRequest.getName()));

    if (existFilmName) {
      log.warn("Ошибка валидации: фильм с именем {} уже добавлен", filmPostRequest.getName());
      throw new ConditionsNotMetException("Данное имя фильма уже занято");
    }

    Film newFilm =
        new Film(
            generateId(),
            filmPostRequest.getName(),
            filmPostRequest.getDescription(),
            filmPostRequest.getReleaseDate(),
            filmPostRequest.getDuration());

    log.debug("Сформирован объект {}", newFilm);

    if (filmPostRequest.getDescription() == null || filmPostRequest.getDescription().isBlank()) {
      log.trace("Поступил пустой Description - устанавливается стандартное значение <->");
      newFilm.setDescription("-");
    }

    filmHashMap.put(newFilm.getId(), newFilm);

    log.info("Объект {} успешно добавлен по ID - {}", newFilm.getName(), newFilm.getId());

    return FilmResponse.builder()
        .id(newFilm.getId())
        .name(newFilm.getName())
        .description(newFilm.getDescription())
        .releaseDate(newFilm.getReleaseDate())
        .duration(newFilm.getDuration())
        .build();
  }

  public FilmResponse updateFilm(FilmPutRequest filmPutRequest) {
    log.trace("Вызывается updateFilm");
    Film oldFilm = filmHashMap.get(filmPutRequest.getId());

    if (oldFilm == null) {
      log.warn("Ошибка валидации: фильм по ID - {} не найден", filmPutRequest.getId());
      throw new UserNotFound(String.format("По ID %d фильм не найден", filmPutRequest.getId()));
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

    return FilmResponse.builder()
        .id(oldFilm.getId())
        .name(oldFilm.getName())
        .description(oldFilm.getDescription())
        .releaseDate(oldFilm.getReleaseDate())
        .duration(oldFilm.getDuration())
        .build();
  }

  private void updateFilmName(FilmPutRequest filmPutRequest, Film oldFilm) {
    log.trace("Вызывается метод updateFilmName");
    boolean existFilmName =
        filmHashMap.values().stream()
            .anyMatch(film -> film.getName().equalsIgnoreCase(filmPutRequest.getName()));

    if (existFilmName) {
      log.warn("Ошибка валидации: имя {} занято для обновления", filmPutRequest.getName());
      throw new ConditionsNotMetException("Данное имя фильма уже занято");
    }
    oldFilm.setName(filmPutRequest.getName());
    log.trace("Фильм успешно обновил имя на: {}", oldFilm.getName());
  }

  private Long generateId() {
    long currentMaxId = filmHashMap.values().stream().mapToLong(Film::getId).max().orElse(0);

    return ++currentMaxId;
  }
}
