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
import ru.yandex.practicum.filmorate.model.dto.filmDto.FilmResponse;

@SpringBootTest
@AutoConfigureMockMvc
public class FilmGetTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @Autowired private FilmService filmService;

  private FilmResponse postResponse;
  private LocalDate date;

  private ResultActions performPostFilm(FilmPostRequest request) throws Exception {
    return mockMvc.perform(
        post("/films")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)));
  }

  private ResultActions performGetFilm() throws Exception {
    return mockMvc.perform(get("/films").accept(MediaType.APPLICATION_JSON));
  }

  @BeforeEach
  void setUp() throws Exception {
    date = LocalDate.of(2000, 12, 12);

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
  void getOneFilm() throws Exception {
    performGetFilm()
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(postResponse.getId()))
        .andExpect(jsonPath("$[0].name").value("TEST"))
        .andExpect(jsonPath("$[0].description").value("-"))
        .andExpect(jsonPath("$[0].releaseDate").value("2000-12-12"))
        .andExpect(jsonPath("$[0].duration").value(120));
  }

  @Test
  void getManyFilms() throws Exception {
    performPostFilm(
        FilmPostRequest.builder()
            .name("TEST1")
            .releaseDate(date)
            .duration(120)
            .genres(Set.of(Genre.DRAMA, Genre.COMEDY))
            .rating(Rating.PG)
            .build());

    performPostFilm(
        FilmPostRequest.builder()
            .name("TEST2")
            .releaseDate(date)
            .duration(120)
            .genres(Set.of(Genre.DRAMA, Genre.COMEDY))
            .rating(Rating.PG)
            .build());

    performGetFilm()
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(3))
        .andExpect(jsonPath("$[0].id").value(1))
        .andExpect(jsonPath("$[1].id").value(2))
        .andExpect(jsonPath("$[2].id").value(3));
  }
}
