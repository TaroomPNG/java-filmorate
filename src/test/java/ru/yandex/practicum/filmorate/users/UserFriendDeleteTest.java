package ru.yandex.practicum.filmorate.users;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.DaoTest;
import ru.yandex.practicum.filmorate.model.User;

class UserFriendDeleteTest extends DaoTest {

  @Test
  void deleteCorrectFriend() {
    User userOne = addUser("test@yandex.ru", "TEST");
    User userTwo = addUser("test2@yandex.ru", "TEST2");
    userStorage.addFriend(userOne.getId(), userTwo.getId());

    userStorage.removeFriend(userOne.getId(), userTwo.getId());

    assertThat(userStorage.isFriend(userOne.getId(), userTwo.getId())).isFalse();
    assertThat(userStorage.getFriends(userOne.getId())).isEmpty();
  }

  @Test
  void deleteFriendIsOneSided() {
    User userOne = addUser("test@yandex.ru", "TEST");
    User userTwo = addUser("test2@yandex.ru", "TEST2");
    userStorage.addFriend(userOne.getId(), userTwo.getId());
    userStorage.addFriend(userTwo.getId(), userOne.getId());

    userStorage.removeFriend(userOne.getId(), userTwo.getId());

    assertThat(userStorage.getFriends(userTwo.getId()))
        .extracting(User::getId)
        .containsExactly(userOne.getId());
  }
}
