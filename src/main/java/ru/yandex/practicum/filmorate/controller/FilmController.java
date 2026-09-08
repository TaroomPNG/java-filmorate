package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import java.util.Collection;
import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.controller.service.FilmService;
import ru.yandex.practicum.filmorate.model.dto.filmDto.FilmPostRequest;
import ru.yandex.practicum.filmorate.model.dto.filmDto.FilmPutRequest;
import ru.yandex.practicum.filmorate.model.dto.filmDto.FilmResponse;

@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {

  @Getter private final FilmService filmService;

  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  public Collection<FilmResponse> getAllFilms() {
    return filmService.getFilms();
  }

  @GetMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public FilmResponse getFilmById(@PathVariable Long id) {
    return filmService.getFilmById(id);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public FilmResponse addFilm(@Valid @RequestBody FilmPostRequest filmPostRequest) {
    return filmService.addFilm(filmPostRequest);
  }

  @PutMapping
  @ResponseStatus(HttpStatus.OK)
  public FilmResponse updateFilm(@Valid @RequestBody FilmPutRequest filmPutRequest) {
    return filmService.updateFilm(filmPutRequest);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public boolean deleteFilm(@PathVariable Long id) {
    return filmService.deleteFilm(id);
  }

  // Like Mapping

  @PutMapping("/{id}/like/{userId}")
  @ResponseStatus(HttpStatus.OK)
  public FilmResponse likeFilm(@PathVariable Long id, @PathVariable Long userId) {
    return filmService.addLikeOnFilm(id, userId);
  }

  @DeleteMapping("/{id}/like/{userId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public FilmResponse deleteLike(@PathVariable Long id, @PathVariable Long userId) {
    return filmService.deleteLikeOnFilm(id, userId);
  }

  @GetMapping("/popular")
  @ResponseStatus(HttpStatus.OK)
  public Collection<FilmResponse> getPopularFilms(
      @RequestParam(required = false, defaultValue = "10") int count,
      @RequestParam(required = false) Integer genreId,
      @RequestParam(required = false) Integer year) {
    return filmService.getTopFilms(count, genreId, year);
  }

  // Search Mapping
  @GetMapping("/search")
  @ResponseStatus(HttpStatus.OK)
  public List<FilmResponse> searchFilms(@RequestParam String query, @RequestParam String by) {
      return filmService.searchFilms(query, by);
  }
}
