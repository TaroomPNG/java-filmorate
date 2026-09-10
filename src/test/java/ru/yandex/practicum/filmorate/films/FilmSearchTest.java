package ru.yandex.practicum.filmorate.films;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.DaoTest;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.dto.directorDto.DirectorPostRequest;
import ru.yandex.practicum.filmorate.model.dto.filmDto.FilmPostRequest;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class FilmSearchTest extends DaoTest {

    private Film addFilmWithDirector(String title, String directorName) {
        DirectorPostRequest request = new DirectorPostRequest();
        request.setName(directorName);

        Director savedDirector = directorStorage.addDirector(request);
        return filmStorage.addFilm(
                FilmPostRequest.builder()
                        .name(title)
                        .releaseDate(DATE)
                        .duration(120)
                        .genres(Set.of(Genre.ACTION_MOVIE, Genre.THRILLER))
                        .mpa(Rating.PG)
                        .directors(Set.of(savedDirector))
                        .build()
        );
    }

    @BeforeEach
    void setUp() {
        addFilmWithDirector("Джон Уик", "Чад Стахелски");
        addFilmWithDirector("Джон Уик 2", "Чад Стахелски");
        addFilmWithDirector("Джон Уик 3", "Чад Стахелски");
        addFilmWithDirector("Мстители", "Джосс Уидон");
    }

    @Test
    void searchFilms_ShouldFindByTitle() {
        List<Film> result = filmStorage.searchFilms("Джон", true, false);
        assertThat(result).hasSize(3);
    }

    @Test
    void searchFilms_ShouldFindByDirector() {
        List<Film> result = filmStorage.searchFilms("Стах", false, true);
        assertThat(result).hasSize(3);
    }

    @Test
    void searchFilms_ShouldFindByTitleOrDirector() {
        List<Film> result = filmStorage.searchFilms("Джон", true, true);
        assertThat(result).hasSize(3);
    }

    @Test
    void searchFilms_ShouldFindByPartialMatch() {
        List<Film> result = filmStorage.searchFilms("Уик", true, false);
        assertThat(result).hasSize(3);
    }

    @Test
    void searchFilms_ShouldTrimQuery() {
        // Ищем уникальный фильм "Мстители"
        List<Film> result = filmStorage.searchFilms("   Мстители   ", true, false);
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Мстители");
    }

    @Test
    void searchFilms_ShouldBeCaseInsensitive() {
        assertThat(filmStorage.searchFilms("мстители", true, false))
                .hasSize(1);
        assertThat(filmStorage.searchFilms("МСТИТЕЛИ", true, false))
                .hasSize(1);
        assertThat(filmStorage.searchFilms("мСТИТЕЛИ", true, false))
                .hasSize(1);
    }
}
