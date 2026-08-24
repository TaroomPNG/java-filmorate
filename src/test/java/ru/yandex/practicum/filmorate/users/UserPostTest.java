package ru.yandex.practicum.filmorate.users;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.dao.DuplicateKeyException;
import ru.yandex.practicum.filmorate.DaoTest;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.dto.userDto.UserPostRequest;

class UserPostTest extends DaoTest {

  @Test
  void addCorrectUser() {
    User user = addUser("test@yandex.ru", "TEST");

    assertThat(user)
        .hasFieldOrPropertyWithValue("email", "test@yandex.ru")
        .hasFieldOrPropertyWithValue("login", "TEST")
        .hasFieldOrPropertyWithValue("name", "TEST")
        .hasFieldOrPropertyWithValue("birthday", DATE);
  }

  @Test
  void add2CorrectUsers() {
    User first = addUser("test@yandex.ru", "TEST");
    User second = addUser("test2@yandex.ru", "TEST2");

    assertThat(first.getId()).isEqualTo(1L);
    assertThat(second.getId()).isEqualTo(2L);
  }

  @Test
  void addUserWithoutName() {
    User user =
        userStorage.addUser(
            UserPostRequest.builder().email("test@yandex.ru").login("TEST").birthday(DATE).build());

    assertThat(user.getName()).isEqualTo("TEST");
  }

  @Test
  void add2UsersWithSameLogin() {
    addUser("test@yandex.ru", "TEST");

    assertThatThrownBy(() -> addUser("test1@yandex.ru", "TEST"))
        .isInstanceOf(DuplicateKeyException.class);
  }

  @Test
  void add2UsersWithSameEmail() {
    addUser("test@yandex.ru", "TEST");

    assertThatThrownBy(() -> addUser("test@yandex.ru", "TEST2"))
        .isInstanceOf(DuplicateKeyException.class);
  }
}
