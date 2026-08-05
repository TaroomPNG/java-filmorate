package ru.yandex.practicum.filmorate.controller.exceptions;

public class UserNotFound extends RuntimeException {
  public UserNotFound(String message) {
    super(message);
  }
}
