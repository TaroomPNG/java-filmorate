package ru.yandex.practicum.filmorate.controller.exceptions;

public class FeedNotFound extends RuntimeException {
  public FeedNotFound(String message) {
    super(message);
  }

  public FeedNotFound(Long id) {
    super(String.format("По ID пользователя - %d feed пуст", id));
  }
}
