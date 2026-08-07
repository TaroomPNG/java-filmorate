package ru.yandex.practicum.filmorate.controller.service.storage;

import java.util.List;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.dto.filmDto.FilmPostRequest;
import ru.yandex.practicum.filmorate.model.dto.filmDto.FilmPutRequest;

interface FilmStorage {
  Film addFilm(FilmPostRequest film);

  Film updateFilm(FilmPutRequest film);

  boolean deleteFilm(long id);

  Film getFilmById(long id);

  List<Film> getFilms();

  boolean isFilmExists(long id);
}
