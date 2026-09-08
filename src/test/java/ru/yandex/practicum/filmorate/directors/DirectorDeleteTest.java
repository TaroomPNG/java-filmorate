package ru.yandex.practicum.filmorate.directors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Set;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.DaoTest;
import ru.yandex.practicum.filmorate.controller.exceptions.DirectorNotFound;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.dto.filmDto.FilmPostRequest;

class DirectorDeleteTest extends DaoTest {

  @Test
  void deleteCorrectDirector() {
    Director created = addDirector("TESTDirector");

    assertThat(directorStorage.deleteDirector(created.getId())).isTrue();
    assertThat(directorStorage.isDirectorExist(created.getId())).isFalse();
  }

  @Test
  void deleteNonexistentDirector() {
    assertThatThrownBy(() -> directorStorage.deleteDirector(9999L))
        .isInstanceOf(DirectorNotFound.class);
  }

  @Test
  void deleteDirectorLinkedToFilm() {
    Director director = addDirector("TESTDirector");
    Film film =
        filmStorage.addFilm(
            FilmPostRequest.builder()
                .name("Jaws")
                .releaseDate(DATE)
                .duration(120)
                .genres(Set.of(Genre.DRAMA))
                .mpa(Rating.PG)
                .directors(Set.of(director))
                .build());

    assertThat(directorStorage.deleteDirector(director.getId())).isTrue();
    assertThat(directorStorage.isDirectorExist(director.getId())).isFalse();
    assertThat(filmStorage.getFilmById(film.getId()).getDirectors()).isEmpty();
  }
}
