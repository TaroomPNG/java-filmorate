package ru.yandex.practicum.filmorate.users;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.DaoTest;
import ru.yandex.practicum.filmorate.controller.exceptions.UserNotFound;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.dto.userDto.UserPutRequest;

class UserPutTest extends DaoTest {

  @Test
  void putCorrectUser() {
    User created = addUser("test@yandex.ru", "TEST");

    User updated =
        userStorage.updateUser(
            UserPutRequest.builder().id(created.getId()).email("Taroom@yandex.ru").build());

    assertThat(updated)
        .hasFieldOrPropertyWithValue("id", created.getId())
        .hasFieldOrPropertyWithValue("email", "Taroom@yandex.ru")
        .hasFieldOrPropertyWithValue("login", "TEST")
        .hasFieldOrPropertyWithValue("name", "TEST")
        .hasFieldOrPropertyWithValue("birthday", DATE);
  }

  @Test
  void putIncorrectUserId() {
    assertThatThrownBy(() -> userStorage.updateUser(UserPutRequest.builder().id(2L).build()))
        .isInstanceOf(UserNotFound.class);
  }

  @Test
  void putUserWithSameLogin() {
    User created = addUser("test@yandex.ru", "TEST");

    User updated =
        userStorage.updateUser(
            UserPutRequest.builder().id(created.getId()).login(created.getLogin()).build());

    assertThat(updated.getLogin()).isEqualTo("TEST");
  }

  @Test
  void putUserWithSameEmail() {
    User created = addUser("test@yandex.ru", "TEST");

    User updated =
        userStorage.updateUser(
            UserPutRequest.builder().id(created.getId()).email(created.getEmail()).build());

    assertThat(updated.getEmail()).isEqualTo("test@yandex.ru");
  }
}
