package ru.yandex.practicum.filmorate.users;

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
import ru.yandex.practicum.filmorate.controller.service.UserService;
import ru.yandex.practicum.filmorate.model.dto.userDto.UserPostRequest;
import ru.yandex.practicum.filmorate.model.dto.userDto.UserPutRequest;
import ru.yandex.practicum.filmorate.model.dto.userDto.UserResponse;

@SpringBootTest
@AutoConfigureMockMvc
public class UserPutTest {

  private final String idPath = "$.id";
  private final String emailPath = "$.email";
  private final String loginPath = "$.login";
  private final String namePath = "$.name";
  private final String birthdayPath = "$.birthday";
  private UserResponse userPostResponse;
  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @Autowired private UserService userService;

  private ResultActions performPutUser(UserPutRequest request) throws Exception {
    return mockMvc.perform(
        put("/users")
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

  @BeforeEach
  void setUp() throws Exception {
    LocalDate date = LocalDate.of(2000, 12, 12);

    MvcResult userResult =
        performPostUser(
                UserPostRequest.builder()
                    .email("test@yandex.ru")
                    .login("TEST")
                    .birthday(date)
                    .name("TEST")
                    .build())
            .andReturn();

    userPostResponse =
        objectMapper.readValue(userResult.getResponse().getContentAsString(), UserResponse.class);
  }

  @AfterEach
  void cleanUp() {
    userService.clearMap();
  }

  @Test
  void putCorrectUser() throws Exception {
    UserPutRequest userPutRequest =
        UserPutRequest.builder().id(userPostResponse.getId()).email("Taroom@yandex.ru").build();

    performPutUser(userPutRequest)
        .andExpect(status().isOk())
        .andExpect(jsonPath(idPath).value(userPostResponse.getId()))
        .andExpect(jsonPath(emailPath).value(userPutRequest.getEmail()))
        .andExpect(jsonPath(loginPath).value(userPostResponse.getLogin()))
        .andExpect(jsonPath(namePath).value(userPostResponse.getName()))
        .andExpect(jsonPath(birthdayPath).value(userPostResponse.getBirthday().toString()));
  }

  // Тест некорректных запросов

  @Test
  void putIncorrectUserId() throws Exception {
    UserPutRequest userPutRequest = UserPutRequest.builder().id(2L).build();

    performPutUser(userPutRequest).andExpect(status().isNotFound());
  }

  @Test
  void putIncorrectUserWithSameLogin() throws Exception {
    UserPutRequest userPutRequest =
        UserPutRequest.builder()
            .id(userPostResponse.getId())
            .login(userPostResponse.getLogin())
            .build();

    performPutUser(userPutRequest).andExpect(status().isBadRequest());
  }

  @Test
  void putIncorrectUserWithSameEmail() throws Exception {
    UserPutRequest userPutRequest =
        UserPutRequest.builder()
            .id(userPostResponse.getId())
            .email(userPostResponse.getEmail())
            .build();

    performPutUser(userPutRequest).andExpect(status().isBadRequest());
  }

  // Проверка валидации параметров

  @Test
  void putIncorrectUserEmail() throws Exception {
    UserPutRequest userPutRequest =
        UserPutRequest.builder().id(userPostResponse.getId()).email("testyandex.ru").build();

    performPutUser(userPutRequest).andExpect(status().isBadRequest());
  }

  @Test
  void putIncorrectUserLogin() throws Exception {
    UserPutRequest userPutRequestWith16Symbols =
        UserPutRequest.builder().id(userPostResponse.getId()).login("a".repeat(16)).build();
    UserPutRequest userPutRequestWithBlank =
        UserPutRequest.builder().id(userPostResponse.getId()).login("  ").build();

    performPutUser(userPutRequestWith16Symbols).andExpect(status().isBadRequest());
    performPutUser(userPutRequestWithBlank).andExpect(status().isBadRequest());
  }

  @Test
  void putIncorrectUserDate() throws Exception {
    UserPutRequest userPutRequest =
        UserPutRequest.builder()
            .id(userPostResponse.getId())
            .birthday(LocalDate.now().plusDays(1))
            .build();

    performPutUser(userPutRequest).andExpect(status().isBadRequest());
  }

  @Test
  void putIncorrectUserName() throws Exception {
    UserPutRequest userPutRequest =
        UserPutRequest.builder().id(userPostResponse.getId()).name("a".repeat(26)).build();

    performPutUser(userPutRequest).andExpect(status().isBadRequest());
  }
}
