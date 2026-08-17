package ru.yandex.practicum.filmorate.films;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import ru.yandex.practicum.filmorate.controller.service.FilmService;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.dto.filmDto.FilmPostRequest;

@SpringBootTest
@AutoConfigureMockMvc
class FilmPostTest {

  private final LocalDate date = LocalDate.of(2000, 12, 12);
  private final String idPath = "$.id";
  private final String namePath = "$.name";
  private final String descriptionPath = "$.description";
  private final String releaseDatePath = "$.releaseDate";
  private final String durationPath = "$.duration";
  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @Autowired private FilmService filmService;

  private ResultActions performPostFilm(FilmPostRequest request) throws Exception {
    return mockMvc.perform(
        post("/films")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)));
  }

  @AfterEach
  void cleanUp() {
    filmService.clearMap();
  }

  @Test
  void addCorrectFilm() throws Exception {
    FilmPostRequest filmPostRequest =
        FilmPostRequest.builder()
            .name("TEST")
            .releaseDate(date)
            .duration(120)
            .genres(Set.of(Genre.DRAMA, Genre.COMEDY))
            .rating(Rating.PG)
            .build();

    performPostFilm(filmPostRequest)
        .andExpect(status().isCreated())
        .andExpect(jsonPath(namePath).value(filmPostRequest.getName()))
        .andExpect(jsonPath(descriptionPath).value(filmPostRequest.getDescription()))
        .andExpect(jsonPath(releaseDatePath).value(filmPostRequest.getReleaseDate().toString()))
        .andExpect(jsonPath(durationPath).value(filmPostRequest.getDuration()));
  }

  @Test
  void add2CorrectFilms() throws Exception {
    FilmPostRequest filmPostRequestFirst =
        FilmPostRequest.builder()
            .name("TEST1")
            .description(null)
            .releaseDate(date)
            .duration(120)
            .genres(Set.of(Genre.DRAMA, Genre.COMEDY))
            .rating(Rating.PG)
            .build();
    FilmPostRequest filmPostRequestSecond =
        FilmPostRequest.builder()
            .name("TEST2")
            .description(null)
            .releaseDate(date)
            .duration(120)
            .genres(Set.of(Genre.DRAMA, Genre.COMEDY))
            .rating(Rating.PG)
            .build();

    performPostFilm(filmPostRequestFirst)
        .andExpect(status().isCreated())
        .andExpect(jsonPath(idPath).value(1));

    performPostFilm(filmPostRequestSecond)
        .andExpect(status().isCreated())
        .andExpect(jsonPath(idPath).value(2));
  }

  @Test
  void addCorrectFilmWithoutDescription() throws Exception {
    FilmPostRequest filmPostRequestWithoutDescription =
        FilmPostRequest.builder()
            .name("NAME")
            .description(null)
            .releaseDate(date)
            .duration(120)
            .genres(Set.of(Genre.DRAMA, Genre.COMEDY))
            .rating(Rating.PG)
            .build();

    FilmPostRequest filmPostRequestWithBlankDescription =
        FilmPostRequest.builder()
            .name("NAME1")
            .description("  ")
            .releaseDate(date)
            .duration(120)
            .genres(Set.of(Genre.DRAMA, Genre.COMEDY))
            .rating(Rating.PG)
            .build();

    String expectedDescription = "-";

    performPostFilm(filmPostRequestWithoutDescription)
        .andExpect(status().isCreated())
        .andExpect(jsonPath(descriptionPath).value(expectedDescription));
    performPostFilm(filmPostRequestWithBlankDescription)
        .andExpect(status().isCreated())
        .andExpect(jsonPath(descriptionPath).value(expectedDescription));
  }

  // Тест пограничных значений

  @Test
  void addCorrectFilmWithEdgeDate() throws Exception {
    LocalDate edgeDate = LocalDate.of(1895, 12, 28);

    FilmPostRequest filmPostRequest =
        FilmPostRequest.builder()
            .name("TEST")
            .description("TEST")
            .releaseDate(edgeDate)
            .duration(120)
            .genres(Set.of(Genre.DRAMA, Genre.COMEDY))
            .rating(Rating.PG)
            .build();

    performPostFilm(filmPostRequest)
        .andExpect(status().isCreated())
        .andExpect(jsonPath(releaseDatePath).value(edgeDate.toString()));
  }

  @Test
  void addCorrectFilmWithEdgeDuration() throws Exception {
    FilmPostRequest filmPostRequest =
        FilmPostRequest.builder()
            .name("TEST")
            .description("TEST")
            .releaseDate(date)
            .duration(1)
            .genres(Set.of(Genre.DRAMA, Genre.COMEDY))
            .rating(Rating.PG)
            .build();

    Integer expectedDuration = 1;

    performPostFilm(filmPostRequest)
        .andExpect(status().isCreated())
        .andExpect(jsonPath(durationPath).value(expectedDuration));
  }

  @Test
  void addCorrectFilmWith200SymbolsOnDescriptions() throws Exception {
    FilmPostRequest filmPostRequest =
        FilmPostRequest.builder()
            .name("TEST")
            .description("a".repeat(200))
            .releaseDate(date)
            .duration(120)
            .genres(Set.of(Genre.DRAMA, Genre.COMEDY))
            .rating(Rating.PG)
            .build();

    String expectedDescriptions = "a".repeat(200);

    performPostFilm(filmPostRequest)
        .andExpect(status().isCreated())
        .andExpect(jsonPath(descriptionPath).value(expectedDescriptions));
  }

  // Тест ошибочных значений

  @Test
  void addIncorrectFilmDate() throws Exception {
    LocalDate incorrectDate = LocalDate.of(1895, 12, 27);

    FilmPostRequest filmPostRequest =
        FilmPostRequest.builder()
            .name("TEST")
            .description("TEST")
            .releaseDate(incorrectDate)
            .duration(120)
            .genres(Set.of(Genre.DRAMA, Genre.COMEDY))
            .rating(Rating.PG)
            .build();

    performPostFilm(filmPostRequest).andExpect(status().isBadRequest());
  }

  @Test
  void add2FilmsWithSameName() throws Exception {
    FilmPostRequest filmPostRequestFirst =
        FilmPostRequest.builder()
            .name("TEST")
            .description(null)
            .releaseDate(date)
            .duration(120)
            .genres(Set.of(Genre.DRAMA, Genre.COMEDY))
            .rating(Rating.PG)
            .build();
    FilmPostRequest filmPostRequestSecond =
        FilmPostRequest.builder()
            .name("TEST")
            .description(null)
            .releaseDate(date)
            .duration(120)
            .genres(Set.of(Genre.DRAMA, Genre.COMEDY))
            .rating(Rating.PG)
            .build();

    performPostFilm(filmPostRequestFirst).andExpect(status().isCreated());
    performPostFilm(filmPostRequestSecond).andExpect(status().isBadRequest());
  }

  // Тест валидации параметров

  @Test
  void addIncorrectFilmWithNullObject() throws Exception {
    performPostFilm(null).andExpect(status().isInternalServerError());
  }

  @Test
  void addIncorrectFilmWithNullDate() throws Exception {
    FilmPostRequest filmPostRequest =
        FilmPostRequest.builder()
            .name("TEST")
            .description("TEST")
            .releaseDate(null)
            .duration(120)
            .genres(Set.of(Genre.DRAMA, Genre.COMEDY))
            .rating(Rating.PG)
            .build();

    performPostFilm(filmPostRequest).andExpect(status().isBadRequest());
  }

  @Test
  void addIncorrectFilmWithZeroDuration() throws Exception {
    FilmPostRequest filmPostRequest =
        FilmPostRequest.builder()
            .name("TEST")
            .description("TEST")
            .releaseDate(date)
            .duration(0)
            .genres(Set.of(Genre.DRAMA, Genre.COMEDY))
            .rating(Rating.PG)
            .build();

    performPostFilm(filmPostRequest).andExpect(status().isBadRequest());
  }

  @Test
  void addIncorrectFilmDescriptionWith201Symbols() throws Exception {
    FilmPostRequest filmPostRequest =
        FilmPostRequest.builder()
            .name("TEST")
            .description("a".repeat(201))
            .releaseDate(date)
            .duration(120)
            .genres(Set.of(Genre.DRAMA, Genre.COMEDY))
            .rating(Rating.PG)
            .build();

    performPostFilm(filmPostRequest).andExpect(status().isBadRequest());
  }

  @Test
  void addIncorrectFilmName() throws Exception {
    FilmPostRequest filmPostRequest =
        FilmPostRequest.builder()
            .name("")
            .description("TEST")
            .releaseDate(date)
            .duration(120)
            .genres(Set.of(Genre.DRAMA, Genre.COMEDY))
            .rating(Rating.PG)
            .build();

    performPostFilm(filmPostRequest).andExpect(status().isBadRequest());
  }
}
