package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import java.util.Collection;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.controller.service.FilmService;
import ru.yandex.practicum.filmorate.model.dto.filmDto.FilmPostRequest;
import ru.yandex.practicum.filmorate.model.dto.filmDto.FilmPutRequest;
import ru.yandex.practicum.filmorate.model.dto.filmDto.FilmResponse;

@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {

  private final FilmService filmService;

  @GetMapping
  public ResponseEntity<Collection<FilmResponse>> getAllFilms() {
    return ResponseEntity.ok(filmService.getFilms());
  }

  @GetMapping("/{id}")
  public ResponseEntity<FilmResponse> getFilmById(@PathVariable Long id) {
    return ResponseEntity.ok(filmService.getFilmById(id));
  }

  @PostMapping
  public ResponseEntity<FilmResponse> addFilm(@Valid @RequestBody FilmPostRequest filmPostRequest) {
    return ResponseEntity.status(HttpStatus.CREATED).body(filmService.addFilm(filmPostRequest));
  }

  @PutMapping
  public ResponseEntity<FilmResponse> updateFilm(@Valid @RequestBody FilmPutRequest filmPutRequest) {
    return ResponseEntity.ok(filmService.updateFilm(filmPutRequest));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Boolean> deleteFilm(@PathVariable Long id) {
    return ResponseEntity.status(HttpStatus.NO_CONTENT).body(filmService.deleteFilm(id));
  }

  // Like Mapping

  @PutMapping("/{id}/like/{userId}")
  public ResponseEntity<FilmResponse> likeFilm(@PathVariable Long id, @PathVariable Long userId) {
    return ResponseEntity.ok(filmService.addLikeOnFilm(id, userId));
  }

  @DeleteMapping("/{id}/like/{userId}")
  public ResponseEntity<FilmResponse> deleteLike(@PathVariable Long id, @PathVariable Long userId) {
    return ResponseEntity.ok(filmService.deleteLikeOnFilm(id, userId));
  }

  @GetMapping("/popular")
  public ResponseEntity<Collection<FilmResponse>> getPopularFilms(
      @RequestParam(required = false, defaultValue = "10") int count,
      @RequestParam(required = false) Integer genreId,
      @RequestParam(required = false) Integer year) {
    return ResponseEntity.ok(filmService.getTopFilms(count, genreId, year));
  }

  @GetMapping("/director/{directorId}")
  public ResponseEntity<Collection<FilmResponse>> getFilmsByDirector(
      @PathVariable Long directorId, @RequestParam String sortBy) {
    return ResponseEntity.ok(filmService.getFilmsByDirector(directorId, sortBy));
  }

  @GetMapping("/common")
  @ResponseStatus(HttpStatus.OK)
  public List<FilmResponse> getCommonFilms(@RequestParam long userId, @RequestParam long friendId) {
      return filmService.getCommonFilms(userId, friendId);
  }
}
