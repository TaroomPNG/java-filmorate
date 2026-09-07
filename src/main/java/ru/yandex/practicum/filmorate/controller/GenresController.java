package ru.yandex.practicum.filmorate.controller;

import java.util.Collection;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.controller.service.FilmService;
import ru.yandex.practicum.filmorate.model.dto.genreDto.GenreResponse;

@RestController
@RequestMapping("/genres")
@RequiredArgsConstructor
public class GenresController {
    private final FilmService filmService;

    @GetMapping
    public ResponseEntity<Collection<GenreResponse>> getGenres() {
        return ResponseEntity.ok(filmService.getGenres());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GenreResponse> getGenreById(@PathVariable long id) {
        return ResponseEntity.ok(filmService.getGenreById(id));
    }
}
