package ru.yandex.practicum.filmorate.controller.exceptions;

public class FilmNotFound extends RuntimeException {
  public FilmNotFound(String message) {
    super(message);
  }

  public FilmNotFound(Long id) {
    super(String.format("По ID %d фильм не найден", id));
  }
}
