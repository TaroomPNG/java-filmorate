package ru.yandex.practicum.filmorate.films;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.DaoTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.dto.filmDto.FilmPostRequest;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class FilmSearchTest extends DaoTest {

    private Film addFilmWithDescription(String title, String description) {
        return filmStorage.addFilm(
                FilmPostRequest.builder()
                    .name(title)
                    .description(description)
                    .releaseDate(DATE)
                    .duration(120)
                    .genres(Set.of(Genre.ACTION_MOVIE,Genre.THRILLER))
                    .mpa(Rating.PG)
                    .build()
        );
    }

    @BeforeEach
    void setUp() {
        addFilmWithDescription("Джон Уик", "Фильм о мести за собаку, последний подарок от жены");
        addFilmWithDescription("Джон Уик 2", "Собака отомщена, но прошлое не даёт покоя");
        addFilmWithDescription("Джон Уик 3", "Месть свершилась, но заявляются призраки прошлого");
        addFilmWithDescription("Мстители", "О супергероях защищающих землю от инопланетной угрозы");
    }

    @Test
    void searchFilms_ShouldFindByDescription() {
        List<Film> result = filmStorage.searchFilms("супер");
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Мстители");
    }

    @Test
    void searchFilms_ShouldFindTwoByDescription() {
        List<Film> result = filmStorage.searchFilms("собак");
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Джон Уик");
    }

    @Test
    void searchFilms_ShouldFindByLongDescription() {
        List<Film> result = filmStorage.searchFilms("Фильм о мести за собаку");
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Джон Уик");
    }

    @Test
    void searchFilms_ShouldFindByTitle() {
        List<Film> result = filmStorage.searchFilms("Джон");
        assertThat(result).hasSize(3);
    }

    @Test
    void searchFilms_ShouldTrimQuery() {
        List<Film> result = filmStorage.searchFilms("    Мстители    ");
        assertThat(result).hasSize(1);
    }

    @Test
    void searchFilms_ShouldBeCaseInsensitive() {
        assertThat(filmStorage.searchFilms("мстители")).hasSize(1);
        assertThat(filmStorage.searchFilms("МСТИТЕЛИ")).hasSize(1);
        assertThat(filmStorage.searchFilms("мСТИТЕЛИ")).hasSize(1);
        assertThat(filmStorage.searchFilms("МСТитЕли")).hasSize(1);
    }

    @Test
    void searchFilms_ShouldReturnEmpty() {
        List<Film> result = filmStorage.searchFilms("l;jy ebr");
        assertThat(result).isEmpty();
    }
}