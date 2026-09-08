package ru.yandex.practicum.filmorate.controller.exceptions;

public class DirectorNotFound extends RuntimeException {
  public DirectorNotFound(Long id) {
    super(String.format("По ID %s режиссер не найден", id));
  }
}
