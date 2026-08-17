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
import ru.yandex.practicum.filmorate.controller.service.UserService;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.dto.filmDto.FilmPostRequest;
import ru.yandex.practicum.filmorate.model.dto.filmDto.FilmResponse;
import ru.yandex.practicum.filmorate.model.dto.userDto.UserPostRequest;
import ru.yandex.practicum.filmorate.model.dto.userDto.UserResponse;

@SpringBootTest
@AutoConfigureMockMvc
public class FilmPopularGetTest {

  private final LocalDate date = LocalDate.of(2000, 12, 12);

  private FilmResponse filmOneLike;
  private FilmResponse filmTwoLikes;
  private FilmResponse filmNoLikes;

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

  private ResultActions performGetPopular(Integer count) throws Exception {
    String url = count == null ? "/films/popular" : "/films/popular?count=" + count;
    return mockMvc.perform(get(url).accept(MediaType.APPLICATION_JSON));
  }

  private FilmResponse createFilm(String name) throws Exception {
    MvcResult result =
        performPostFilm(
                FilmPostRequest.builder()
                    .name(name)
                    .releaseDate(date)
                    .duration(120)
                    .genres(Set.of(Genre.DRAMA, Genre.COMEDY))
                    .rating(Rating.PG)
                    .build())
            .andReturn();
    return objectMapper.readValue(result.getResponse().getContentAsString(), FilmResponse.class);
  }

  private UserResponse createUser(String email, String login) throws Exception {
    MvcResult result =
        performPostUser(
                UserPostRequest.builder()
                    .email(email)
                    .login(login)
                    .birthday(date)
                    .name(login)
                    .build())
            .andReturn();
    return objectMapper.readValue(result.getResponse().getContentAsString(), UserResponse.class);
  }

  @BeforeEach
  void setUp() throws Exception {
    filmTwoLikes = createFilm("FILM_TWO_LIKES");
    filmOneLike = createFilm("FILM_ONE_LIKE");
    filmNoLikes = createFilm("FILM_NO_LIKES");

    UserResponse userOne = createUser("test@yandex.ru", "TEST");
    UserResponse userTwo = createUser("test2@yandex.ru", "TEST2");

    performLikeFilm(filmTwoLikes.getId(), userOne.getId()).andExpect(status().isOk());
    performLikeFilm(filmTwoLikes.getId(), userTwo.getId()).andExpect(status().isOk());
    performLikeFilm(filmOneLike.getId(), userOne.getId()).andExpect(status().isOk());
  }

  @AfterEach
  void cleanUp() {
    filmService.clearMap();
    userService.clearMap();
  }

  @Test
  void getPopularFilmsOrderedByLikesDescending() throws Exception {
    performGetPopular(null)
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(3))
        .andExpect(jsonPath("$[0].id").value(filmTwoLikes.getId()))
        .andExpect(jsonPath("$[1].id").value(filmOneLike.getId()))
        .andExpect(jsonPath("$[2].id").value(filmNoLikes.getId()));
  }

  @Test
  void getPopularFilmsWithCountLimit() throws Exception {
    performGetPopular(1)
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(1))
        .andExpect(jsonPath("$[0].id").value(filmTwoLikes.getId()));
  }

  @Test
  void getPopularFilmsWithCountLargerThanAvailable() throws Exception {
    performGetPopular(50).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(3));
  }
}
