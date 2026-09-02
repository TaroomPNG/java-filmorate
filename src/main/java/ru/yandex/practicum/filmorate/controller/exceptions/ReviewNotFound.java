package ru.yandex.practicum.filmorate.controller.exceptions;

public class ReviewNotFound extends RuntimeException {
  public ReviewNotFound(String message) {
    super(message);
  }

  public ReviewNotFound(Long id) {
    super(String.format("По ID %d review не найден", id));
  }
}
