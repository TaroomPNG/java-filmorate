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
public class UserFriendGetTest {

  private final LocalDate date = LocalDate.of(2000, 12, 12);

  private UserResponse userOne;
  private UserResponse userTwo;
  private UserResponse userThree;

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

  private ResultActions performGetCommonFriends(Long id, Long otherId) throws Exception {
    return mockMvc.perform(
        get("/users/{id}/friends/common/{otherId}", id, otherId)
            .accept(MediaType.APPLICATION_JSON));
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
    userOne = createUser("test@yandex.ru", "TEST");
    userTwo = createUser("test2@yandex.ru", "TEST2");
    userThree = createUser("test3@yandex.ru", "TEST3");
  }

  @AfterEach
  void cleanUp() {
    userService.clearMap();
  }

  @Test
  void getAllFriendsEmpty() throws Exception {
    performGetFriends(userOne.getId())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(0));
  }

  @Test
  void getAllFriendsAfterAdd() throws Exception {
    performAddFriend(userTwo.getId(), userOne.getId());
    performAddFriend(userOne.getId(), userTwo.getId()).andExpect(status().isOk());

    performGetFriends(userOne.getId())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(1))
        .andExpect(jsonPath("$[0].id").value(userTwo.getId()));
  }

  @Test
  void getAllFriendsNonexistentUser() throws Exception {
    performGetFriends(9999L).andExpect(status().isNotFound());
  }

  @Test
  void getCommonFriendsEmpty() throws Exception {
    performGetCommonFriends(userOne.getId(), userTwo.getId())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(0));
  }

  @Test
  void getCommonFriendsWithOneCommon() throws Exception {
    performAddFriend(userOne.getId(), userThree.getId()).andExpect(status().isOk());
    performAddFriend(userTwo.getId(), userThree.getId()).andExpect(status().isOk());

    performAddFriend(userThree.getId(), userOne.getId()).andExpect(status().isOk());
    performAddFriend(userThree.getId(), userTwo.getId()).andExpect(status().isOk());

    performGetCommonFriends(userOne.getId(), userTwo.getId())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(1))
        .andExpect(jsonPath("$[0].id").value(userThree.getId()));
  }

  // Тест ошибочных значений

  @Test
  void getCommonFriendsNonexistentUser() throws Exception {
    performGetCommonFriends(9999L, userTwo.getId()).andExpect(status().isNotFound());
  }

  @Test
  void getCommonFriendsNonexistentOtherId() throws Exception {
    performGetCommonFriends(userOne.getId(), 9999L).andExpect(status().isNotFound());
  }
}
