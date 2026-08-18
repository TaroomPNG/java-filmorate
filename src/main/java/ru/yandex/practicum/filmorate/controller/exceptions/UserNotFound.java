package ru.yandex.practicum.filmorate.controller.exceptions;

public class UserNotFound extends RuntimeException {
  public UserNotFound(String message) {
    super(message);
  }

  public UserNotFound(Long id) {
    super(String.format("По ID %d user не найден", id));
  }
}
