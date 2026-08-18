package ru.yandex.practicum.filmorate.users;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import ru.yandex.practicum.filmorate.controller.service.UserService;
import ru.yandex.practicum.filmorate.model.dto.userDto.UserPostRequest;

@SpringBootTest
@AutoConfigureMockMvc
public class UserPostTest {

  private final String idPath = "$.id";
  private final String emailPath = "$.email";
  private final String loginPath = "$.login";
  private final String namePath = "$.name";
  private final String birthdayPath = "$.birthday";
  private final LocalDate date = LocalDate.of(2000, 12, 12);
  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @Autowired private UserService userService;

  private ResultActions performPostUser(UserPostRequest userPostRequest) throws Exception {
    return mockMvc.perform(
        post("/users")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(userPostRequest)));
  }

  @AfterEach
  void cleanUp() {
    userService.clearMap();
  }

  @Test
  void addCorrectUser() throws Exception {
    UserPostRequest userPostRequest =
        UserPostRequest.builder()
            .email("test@yandex.ru")
            .login("TEST")
            .birthday(date)
            .name("TEST")
            .build();

    performPostUser(userPostRequest)
        .andExpect(status().isCreated())
        .andExpect(jsonPath(emailPath).value(userPostRequest.getEmail()))
        .andExpect(jsonPath(loginPath).value(userPostRequest.getLogin()))
        .andExpect(jsonPath(birthdayPath).value(userPostRequest.getBirthday().toString()))
        .andExpect(jsonPath(namePath).value(userPostRequest.getName()));
  }

  @Test
  void add2CorrectUsers() throws Exception {
    UserPostRequest userPostRequestFirst =
        UserPostRequest.builder()
            .email("test@yandex.ru")
            .login("TEST")
            .birthday(date)
            .name("TEST")
            .build();
    UserPostRequest userPostRequestSecond =
        UserPostRequest.builder()
            .email("test2@yandex.ru")
            .login("TEST2")
            .birthday(date)
            .name("TEST")
            .build();

    performPostUser(userPostRequestFirst)
        .andExpect(status().isCreated())
        .andExpect(jsonPath(idPath).value(1));

    performPostUser(userPostRequestSecond)
        .andExpect(status().isCreated())
        .andExpect(jsonPath(idPath).value(2));
  }

  @Test
  void addUserWithoutName() throws Exception {
    UserPostRequest userPostRequest =
        UserPostRequest.builder().email("test@yandex.ru").login("TEST").birthday(date).build();

    performPostUser(userPostRequest)
        .andExpect(status().isCreated())
        .andExpect(jsonPath(namePath).value(userPostRequest.getLogin()));
  }

  // Тест ошибочных значений
  @Test
  void add2UsersWithSameLogin() throws Exception {
    UserPostRequest userPostRequestFirst =
        UserPostRequest.builder().email("test@yandex.ru").login("TEST").birthday(date).build();

    UserPostRequest userPostRequestSecond =
        UserPostRequest.builder().email("test1@yandex.ru").login("TEST").birthday(date).build();

    performPostUser(userPostRequestFirst)
        .andExpect(status().isCreated())
        .andExpect(jsonPath(loginPath).value(userPostRequestFirst.getLogin()));
    performPostUser(userPostRequestSecond).andExpect(status().isBadRequest());
  }

  @Test
  void add2UsersWithSameEmail() throws Exception {
    UserPostRequest userPostRequestFirst =
        UserPostRequest.builder().email("test@yandex.ru").login("TEST").birthday(date).build();
    UserPostRequest userPostRequestSecond =
        UserPostRequest.builder().email("test@yandex.ru").login("TEST2").birthday(date).build();

    performPostUser(userPostRequestFirst)
        .andExpect(status().isCreated())
        .andExpect(jsonPath(emailPath).value(userPostRequestFirst.getEmail()));
    performPostUser(userPostRequestSecond).andExpect(status().isBadRequest());
  }

  // Тест валидации параметров

  @Test
  void addNullUserObject() throws Exception {
    performPostUser(null).andExpect(status().isInternalServerError());
  }

  @Test
  void addIncorrectUserEmail() throws Exception {
    UserPostRequest userPostRequest =
        UserPostRequest.builder().email("testyandex.ru").login("TEST").birthday(date).build();

    performPostUser(userPostRequest).andExpect(status().isBadRequest());
  }

  @Test
  void addIncorrectUserLogin() throws Exception {
    UserPostRequest userPostRequest =
        UserPostRequest.builder().email("test@yandex.ru").login("  ").birthday(date).build();

    performPostUser(userPostRequest).andExpect(status().isBadRequest());
  }

  @Test
  void addUserWithIncorrectDate() throws Exception {
    UserPostRequest userPostRequest =
        UserPostRequest.builder()
            .email("test@yandex.ru")
            .login("TEST")
            .birthday(LocalDate.now().plusDays(1))
            .name("TEST")
            .build();

    performPostUser(userPostRequest).andExpect(status().isBadRequest());
  }
}
