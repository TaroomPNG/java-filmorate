package ru.yandex.practicum.filmorate.controller.exceptions;

public class DuplicationExceptions extends RuntimeException {
  public DuplicationExceptions(String message) {
    super(message);
  }
}
