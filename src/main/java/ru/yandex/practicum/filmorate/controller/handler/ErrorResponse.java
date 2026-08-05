package ru.yandex.practicum.filmorate.controller.handler;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ErrorResponse {
  private String message;
}
