package ru.yandex.practicum.filmorate.users;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.DaoTest;
import ru.yandex.practicum.filmorate.model.User;

class UserDeleteTest extends DaoTest {

  @Test
  void deleteCorrectUser() {
    User created = addUser("del@yandex.ru", "DEL");

    assertThat(userStorage.deleteUser(created.getId())).isTrue();
    assertThat(userStorage.isUserExists(created.getId())).isFalse();
  }

  @Test
  void deleteNonexistentUser() {
    assertThat(userStorage.deleteUser(9999L)).isFalse();
  }
}
