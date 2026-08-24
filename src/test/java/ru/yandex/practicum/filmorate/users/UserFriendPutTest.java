package ru.yandex.practicum.filmorate.users;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.DaoTest;
import ru.yandex.practicum.filmorate.model.User;

class UserFriendPutTest extends DaoTest {

  @Test
  void addCorrectFriend() {
    User userOne = addUser("test@yandex.ru", "TEST");
    User userTwo = addUser("test2@yandex.ru", "TEST2");

    userStorage.addFriend(userOne.getId(), userTwo.getId());

    assertThat(userStorage.isFriend(userOne.getId(), userTwo.getId())).isTrue();
    assertThat(userStorage.getFriends(userOne.getId()))
        .extracting(User::getId)
        .containsExactly(userTwo.getId());
    assertThat(userStorage.getFriends(userTwo.getId())).isEmpty();
  }

  @Test
  void addFriendTwice() {
    User userOne = addUser("test@yandex.ru", "TEST");
    User userTwo = addUser("test2@yandex.ru", "TEST2");

    userStorage.addFriend(userOne.getId(), userTwo.getId());
    userStorage.addFriend(userOne.getId(), userTwo.getId());

    assertThat(userStorage.getFriends(userOne.getId())).hasSize(1);
  }
}
