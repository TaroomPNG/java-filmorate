package ru.yandex.practicum.filmorate.controller;

import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.controller.service.FilmService;
import ru.yandex.practicum.filmorate.model.Rating;

@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
public class MpaController {
  private final FilmService filmService;

  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  public Collection<Rating> getRatings() {
    return filmService.getRatings();
  }

  @GetMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public Rating getRatingById(@PathVariable long id) {
    return filmService.getRatingById(id);
  }
}
