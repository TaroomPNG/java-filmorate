package ru.yandex.practicum.filmorate.controller.exceptions;

public class ConditionsNotMetException extends RuntimeException {
  public ConditionsNotMetException(String message) {
    super(message);
  }
}
