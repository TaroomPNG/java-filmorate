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
public class UserFriendPutTest {

  private final String idPath = "$.id";
  private final String friendSetLengthPath = "$.friendSet.length()";
  private final String firstFriendIdPath = "$.friendSet[0].id";
  private final LocalDate date = LocalDate.of(2000, 12, 12);

  private UserResponse userOne;
  private UserResponse userTwo;

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @Autowired private UserService userService;

  private ResultActions performPostUser(UserPostRequest request) throws Exception {
    return mockMvc.perform(
        post("/users")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)));
  }

  private ResultActions performAddFriend(Long id, Long friendId) throws Exception {
    return mockMvc.perform(
        put("/users/{id}/friends/{friendId}", id, friendId).accept(MediaType.APPLICATION_JSON));
  }

  private ResultActions performGetFriends(Long id) throws Exception {
    return mockMvc.perform(get("/users/{id}/friends", id).accept(MediaType.APPLICATION_JSON));
  }

  @BeforeEach
  void setUp() throws Exception {
    MvcResult resultOne =
        performPostUser(
                UserPostRequest.builder()
                    .email("test@yandex.ru")
                    .login("TEST")
                    .birthday(date)
                    .name("TEST")
                    .build())
            .andReturn();
    userOne =
        objectMapper.readValue(resultOne.getResponse().getContentAsString(), UserResponse.class);

    MvcResult resultTwo =
        performPostUser(
                UserPostRequest.builder()
                    .email("test2@yandex.ru")
                    .login("TEST2")
                    .birthday(date)
                    .name("TEST2")
                    .build())
            .andReturn();
    userTwo =
        objectMapper.readValue(resultTwo.getResponse().getContentAsString(), UserResponse.class);
  }

  @AfterEach
  void cleanUp() {
    userService.clearMap();
  }

  @Test
  void addCorrectFriend() throws Exception {
    performAddFriend(userOne.getId(), userTwo.getId())
        .andExpect(status().isOk())
        .andExpect(jsonPath(idPath).value(userOne.getId()))
        .andExpect(jsonPath(friendSetLengthPath).value(1))
        .andExpect(jsonPath(firstFriendIdPath).value(userTwo.getId()));
  }

  @Test
  void addFriendIsMutual() throws Exception {
    performAddFriend(userOne.getId(), userTwo.getId()).andExpect(status().isOk());

    performGetFriends(userTwo.getId())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(1))
        .andExpect(jsonPath("$[0].id").value(userOne.getId()));
  }

  @Test
  void addFriendTwiceIsIdempotent() throws Exception {
    performAddFriend(userOne.getId(), userTwo.getId()).andExpect(status().isOk());

    performAddFriend(userOne.getId(), userTwo.getId())
        .andExpect(status().isOk())
        .andExpect(jsonPath(friendSetLengthPath).value(1));
  }

  @Test
  void addSelfAsFriend() throws Exception {
    performAddFriend(userOne.getId(), userOne.getId())
        .andExpect(status().isOk())
        .andExpect(jsonPath(friendSetLengthPath).value(1))
        .andExpect(jsonPath(firstFriendIdPath).value(userOne.getId()));
  }

  // Тест ошибочных значений

  @Test
  void addFriendWithNonexistentFriendId() throws Exception {
    performAddFriend(userOne.getId(), 9999L).andExpect(status().isNotFound());
  }

  @Test
  void addFriendWithNonexistentUserId() throws Exception {
    performAddFriend(9999L, userTwo.getId()).andExpect(status().isNotFound());
  }
}
