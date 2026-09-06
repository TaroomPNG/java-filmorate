package ru.yandex.practicum.filmorate.controller.service.storage;

import java.util.List;
import java.util.Set;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.dto.filmDto.FilmPostRequest;
import ru.yandex.practicum.filmorate.model.dto.filmDto.FilmPutRequest;

public interface FilmStorage {
  Film addFilm(FilmPostRequest filmPostRequest);

  Film updateFilm(FilmPutRequest filmPutRequest);

  boolean deleteFilm(long id);

  Film getFilmById(long id);

  List<Film> getFilms();

  boolean isFilmExists(long id);

  Set<Genre> getFilmGenres(long id);

  List<Genre> getGenres();

  Genre getGenreById(long id);

  List<Rating> getRatings();

  Rating getRatingById(long id);

  boolean isGenreExists(long id);

  boolean isRatingExists(long id);

  void addLike(long filmId, long userId);

  void removeLike(long filmId, long userId);

  List<Film> getPopular(int count);

  void clear();

  List<Film> searchFilms(String query);
}
