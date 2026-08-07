package ru.yandex.practicum.filmorate.films;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
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
import ru.yandex.practicum.filmorate.controller.service.UserService;
import ru.yandex.practicum.filmorate.model.dto.filmDto.FilmPostRequest;
import ru.yandex.practicum.filmorate.model.dto.filmDto.FilmResponse;
import ru.yandex.practicum.filmorate.model.dto.userDto.UserPostRequest;
import ru.yandex.practicum.filmorate.model.dto.userDto.UserResponse;

@SpringBootTest
@AutoConfigureMockMvc
public class FilmLikeDeleteTest {

  private final String idPath = "$.id";
  private final String likeSetLengthPath = "$.likeIds.length()";
  private final LocalDate date = LocalDate.of(2000, 12, 12);

  private FilmResponse film;
  private UserResponse userOne;
  private UserResponse userTwo;

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @Autowired private FilmService filmService;
  @Autowired private UserService userService;

  private ResultActions performPostFilm(FilmPostRequest request) throws Exception {
    return mockMvc.perform(
        post("/films")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)));
  }

  private ResultActions performPostUser(UserPostRequest request) throws Exception {
    return mockMvc.perform(
        post("/users")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)));
  }

  private ResultActions performLikeFilm(Long filmId, Long userId) throws Exception {
    return mockMvc.perform(
        put("/films/{id}/like/{userId}", filmId, userId).accept(MediaType.APPLICATION_JSON));
  }

  private ResultActions performDeleteLike(Long filmId, Long userId) throws Exception {
    return mockMvc.perform(
        delete("/films/{id}/like/{userId}", filmId, userId).accept(MediaType.APPLICATION_JSON));
  }

  @BeforeEach
  void setUp() throws Exception {
    MvcResult filmResult =
        performPostFilm(
                FilmPostRequest.builder().name("TEST").releaseDate(date).duration(120).build())
            .andReturn();
    film =
        objectMapper.readValue(filmResult.getResponse().getContentAsString(), FilmResponse.class);

    MvcResult userOneResult =
        performPostUser(
                UserPostRequest.builder()
                    .email("test@yandex.ru")
                    .login("TEST")
                    .birthday(date)
                    .name("TEST")
                    .build())
            .andReturn();
    userOne =
        objectMapper.readValue(
            userOneResult.getResponse().getContentAsString(), UserResponse.class);

    MvcResult userTwoResult =
        performPostUser(
                UserPostRequest.builder()
                    .email("test2@yandex.ru")
                    .login("TEST2")
                    .birthday(date)
                    .name("TEST2")
                    .build())
            .andReturn();
    userTwo =
        objectMapper.readValue(
            userTwoResult.getResponse().getContentAsString(), UserResponse.class);

    performLikeFilm(film.getId(), userOne.getId()).andExpect(status().isOk());
  }

  @AfterEach
  void cleanUp() {
    filmService.clearMap();
    userService.clearMap();
  }

  @Test
  void deleteCorrectLike() throws Exception {
    performDeleteLike(film.getId(), userOne.getId())
        .andExpect(status().isNoContent())
        .andExpect(jsonPath(idPath).value(film.getId()))
        .andExpect(jsonPath(likeSetLengthPath).value(0));
  }

  @Test
  void deleteLikeThatDoesNotExistKeepsOtherLikes() throws Exception {
    performDeleteLike(film.getId(), userTwo.getId())
        .andExpect(status().isNoContent())
        .andExpect(jsonPath(likeSetLengthPath).value(1));
  }

  // Тест ошибочных значений

  @Test
  void deleteLikeWithNonexistentFilm() throws Exception {
    performDeleteLike(9999L, userOne.getId()).andExpect(status().isNotFound());
  }

  @Test
  void deleteLikeWithNonexistentUser() throws Exception {
    performDeleteLike(film.getId(), 9999L).andExpect(status().isNotFound());
  }
}
