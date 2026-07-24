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
import ru.yandex.practicum.filmorate.model.dto.userDto.UserResponse;

@SpringBootTest
@AutoConfigureMockMvc
public class UserGetTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @Autowired private UserService userService;

  private UserResponse userPostResponse;
  private LocalDate date;

  private ResultActions performPostUser(UserPostRequest request) throws Exception {
    return mockMvc.perform(
        post("/users")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)));
  }

  private ResultActions performGetUser() throws Exception {
    return mockMvc.perform(get("/users").accept(MediaType.APPLICATION_JSON));
  }

  @BeforeEach
  void setUp() throws Exception {
    date = LocalDate.of(2000, 12, 12);

    MvcResult result =
        performPostUser(
                UserPostRequest.builder()
                    .email("test@yandex.ru")
                    .login("TEST")
                    .birthday(date)
                    .name("TEST")
                    .build())
            .andReturn();

    userPostResponse =
        objectMapper.readValue(result.getResponse().getContentAsString(), UserResponse.class);
  }

  @AfterEach
  void cleanUp() {
    userService.clearMap();
  }

  @Test
  void getOneUser() throws Exception {
    performGetUser()
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(userPostResponse.getId()))
        .andExpect(jsonPath("$[0].login").value("TEST"))
        .andExpect(jsonPath("$[0].birthday").value(date.toString()))
        .andExpect(jsonPath("$[0].name").value("TEST"));
  }

  @Test
  void getManyUsers() throws Exception {
    performPostUser(
        UserPostRequest.builder()
            .email("test1@yandex.ru")
            .login("TEST1")
            .birthday(date)
            .name("TEST1")
            .build());

    performPostUser(
        UserPostRequest.builder()
            .email("test2@yandex.ru")
            .login("TEST2")
            .birthday(date)
            .name("TEST2")
            .build());

    performGetUser()
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(3))
        .andExpect(jsonPath("$[0].id").value(1))
        .andExpect(jsonPath("$[1].id").value(2))
        .andExpect(jsonPath("$[2].id").value(3));
  }
}
