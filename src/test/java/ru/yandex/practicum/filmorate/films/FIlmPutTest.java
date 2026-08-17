package ru.yandex.practicum.filmorate.films;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import ru.yandex.practicum.filmorate.controller.service.FilmService;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.dto.filmDto.FilmPostRequest;
import ru.yandex.practicum.filmorate.model.dto.filmDto.FilmPutRequest;
import ru.yandex.practicum.filmorate.model.dto.filmDto.FilmResponse;

@SpringBootTest
@AutoConfigureMockMvc
public class FIlmPutTest {

  private final String idPath = "$.id";
  private final String namePath = "$.name";
  private final String descriptionPath = "$.description";
  private final String releaseDatePath = "$.releaseDate";
  private final String durationPath = "$.duration";
  private FilmResponse postResponse;
  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @Autowired private FilmService filmService;

  private ResultActions performPutFilm(FilmPutRequest request) throws Exception {
    return mockMvc.perform(
        put("/films")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)));
  }

  private ResultActions performPostFilm(FilmPostRequest request) throws Exception {
    return mockMvc.perform(
        post("/films")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)));
  }

  @BeforeEach
  void setUp() throws Exception {
    LocalDate date = LocalDate.of(2000, 12, 12);

    MvcResult postResult =
        performPostFilm(
                FilmPostRequest.builder()
                    .name("TEST")
                    .releaseDate(date)
                    .duration(120)
                    .genres(Set.of(Genre.DRAMA, Genre.COMEDY))
                    .rating(Rating.PG)
                    .build())
            .andReturn();

    postResponse =
        objectMapper.readValue(postResult.getResponse().getContentAsString(), FilmResponse.class);
  }

  @AfterEach
  void cleanUp() {
    filmService.clearMap();
  }

  @Test
  void putCorrectFilm() throws Exception {
    String newName = "TEST_CHANGE";
    FilmPutRequest filmPutRequest =
        FilmPutRequest.builder().id(postResponse.getId()).name(newName).build();

    performPutFilm(filmPutRequest)
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value(filmPutRequest.getName()))
        .andExpect(jsonPath("$.description").value(postResponse.getDescription()))
        .andExpect(jsonPath("$.releaseDate").value(postResponse.getReleaseDate().toString()))
        .andExpect(jsonPath("$.duration").value(postResponse.getDuration()));
  }

  // Тест некорректных запросов

  @Test
  void putIncorrectFilmId() throws Exception {
    FilmPutRequest filmPutRequest = FilmPutRequest.builder().id(2L).build();

    performPutFilm(filmPutRequest).andExpect(status().isNotFound());
  }

  @Test
  void putIncorrectFilmWithSameFilmNames() throws Exception {
    FilmPutRequest filmPutRequest =
        FilmPutRequest.builder().id(postResponse.getId()).name(postResponse.getName()).build();

    performPutFilm(filmPutRequest).andExpect(status().isBadRequest());
  }

  @Test
  void putFilmWithIncorrectDate() throws Exception {
    FilmPutRequest filmPutRequest =
        FilmPutRequest.builder()
            .id(postResponse.getId())
            .releaseDate(LocalDate.of(1895, 12, 27))
            .build();

    performPutFilm(filmPutRequest).andExpect(status().isBadRequest());
  }

  // Проверка валидации параметров

  @Test
  void putNullObject() throws Exception {
    performPutFilm(null).andExpect(status().isInternalServerError());
  }

  @Test
  void putFilmIncorrectDescriptionWith201Symbols() throws Exception {
    FilmPutRequest filmPutRequest =
        FilmPutRequest.builder().id(postResponse.getId()).description("a".repeat(201)).build();

    performPutFilm(filmPutRequest).andExpect(status().isBadRequest());
  }

  @Test
  void putFilmWithFutureDate() throws Exception {
    FilmPutRequest filmPutRequest =
        FilmPutRequest.builder()
            .id(postResponse.getId())
            .releaseDate(LocalDate.of(3000, 12, 12))
            .build();

    performPutFilm(filmPutRequest).andExpect(status().isBadRequest());
  }

  @Test
  void putFilmWithNegativeDuration() throws Exception {
    FilmPutRequest filmPutRequest =
        FilmPutRequest.builder().id(postResponse.getId()).duration(-12).build();

    performPutFilm(filmPutRequest).andExpect(status().isBadRequest());
  }
}
