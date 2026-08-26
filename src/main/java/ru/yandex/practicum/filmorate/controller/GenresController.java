package ru.yandex.practicum.filmorate.controller;

import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.controller.service.FilmService;
import ru.yandex.practicum.filmorate.model.dto.genreDto.GenreResponse;

@RestController
@RequestMapping("/genres")
@RequiredArgsConstructor
public class GenresController {
  private final FilmService filmService;

  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  public Collection<GenreResponse> getGenres() {
    return filmService.getGenres();
  }

  @GetMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public GenreResponse getGenreById(@PathVariable long id) {
    return filmService.getGenreById(id);
  }
}
