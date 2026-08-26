package ru.yandex.practicum.filmorate.controller.exceptions;

public class InternalServerException extends RuntimeException {
  public InternalServerException(String message) {
    super(message);
  }
}
