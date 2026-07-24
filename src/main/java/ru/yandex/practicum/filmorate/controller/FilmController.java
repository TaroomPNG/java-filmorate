package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import java.util.Collection;
import lombok.Getter;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.controller.service.FilmService;
import ru.yandex.practicum.filmorate.model.dto.filmDto.FilmPostRequest;
import ru.yandex.practicum.filmorate.model.dto.filmDto.FilmPutRequest;
import ru.yandex.practicum.filmorate.model.dto.filmDto.FilmResponse;

@RestController
@RequestMapping("/films")
public class FilmController {
  @Getter private final FilmService filmService;

  public FilmController(FilmService filmService) {
    this.filmService = filmService;
  }

  @GetMapping
  public Collection<FilmResponse> getAllFilms() {
    return filmService.getAllFilms();
  }

  @GetMapping("/{id}")
  public FilmResponse getFilm(@PathVariable Long id) {
    return filmService.getFilm(id);
  }

  @PostMapping
  public FilmResponse addFilm(@Valid @RequestBody FilmPostRequest filmPostRequest) {
    return filmService.addFilm(filmPostRequest);
  }

  @PutMapping
  public FilmResponse updateFilm(@Valid @RequestBody FilmPutRequest filmPutRequest) {
    return filmService.updateFilm(filmPutRequest);
  }
}
