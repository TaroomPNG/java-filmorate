package ru.yandex.practicum.filmorate.users;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.DaoTest;
import ru.yandex.practicum.filmorate.controller.exceptions.UserNotFound;
import ru.yandex.practicum.filmorate.model.User;

class UserGetTest extends DaoTest {

  @Test
  void getOneUser() {
    User created = addUser("test@yandex.ru", "TEST");

    List<User> users = userStorage.getUsers();

    assertThat(users).hasSize(1);
    assertThat(users.getFirst())
        .hasFieldOrPropertyWithValue("id", created.getId())
        .hasFieldOrPropertyWithValue("login", "TEST")
        .hasFieldOrPropertyWithValue("name", "TEST")
        .hasFieldOrPropertyWithValue("birthday", DATE);
  }

  @Test
  void getManyUsers() {
    addUser("test@yandex.ru", "TEST");
    addUser("test1@yandex.ru", "TEST1");
    addUser("test2@yandex.ru", "TEST2");

    List<User> users = userStorage.getUsers();

    assertThat(users).extracting(User::getId).containsExactly(1L, 2L, 3L);
  }

  @Test
  void getUserById() {
    User created = addUser("test@yandex.ru", "TEST");

    User user = userStorage.getUserById(created.getId());

    assertThat(user)
        .hasFieldOrPropertyWithValue("id", created.getId())
        .hasFieldOrPropertyWithValue("login", "TEST")
        .hasFieldOrPropertyWithValue("email", "test@yandex.ru");
  }

  @Test
  void getUserByIdNotFound() {
    assertThatThrownBy(() -> userStorage.getUserById(9999L)).isInstanceOf(UserNotFound.class);
  }

  @Test
  void isUserExists() {
    User created = addUser("exists@yandex.ru", "EXISTS");

    assertThat(userStorage.isUserExists(created.getId())).isTrue();
    assertThat(userStorage.isUserExists(9999L)).isFalse();
  }
}
