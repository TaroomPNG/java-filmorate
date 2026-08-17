package ru.yandex.practicum.filmorate.controller.handler;

import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.yandex.practicum.filmorate.controller.exceptions.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.controller.exceptions.DuplicationExceptions;
import ru.yandex.practicum.filmorate.controller.exceptions.FilmNotFound;
import ru.yandex.practicum.filmorate.controller.exceptions.UserNotFound;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  @ExceptionHandler(DuplicationExceptions.class)
  public ResponseEntity<ErrorResponse> handleDuplication(DuplicationExceptions ex) {
    String message = ex.getMessage();
    ErrorResponse response = new ErrorResponse(message);

    log.error(response.toString());
    return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
  }

  @ExceptionHandler({UserNotFound.class, FilmNotFound.class})
  public ResponseEntity<ErrorResponse> handleUserNotFound(RuntimeException ex) {
    String message = ex.getMessage();
    ErrorResponse response = new ErrorResponse(message);

    log.error(response.toString());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
  }

  @ExceptionHandler(ConditionsNotMetException.class)
  public ResponseEntity<ErrorResponse> handleConditionsNotMet(ConditionsNotMetException ex) {
    String message = ex.getMessage();
    ErrorResponse response = new ErrorResponse(message);

    log.error(response.toString());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValid(MethodArgumentNotValidException ex) {
    String message =
        ex.getBindingResult().getFieldErrors().stream()
            .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
            .collect(Collectors.joining("; "));
    ErrorResponse response = new ErrorResponse(message);

    log.error(response.toString());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleException(Exception ex) {
    String message = ex.getMessage();
    ErrorResponse response = new ErrorResponse(message);

    log.error(response.toString());
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
  }
}
