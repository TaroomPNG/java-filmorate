package ru.yandex.practicum.filmorate.users;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.DaoTest;
import ru.yandex.practicum.filmorate.model.User;

class UserFriendGetTest extends DaoTest {

  @Test
  void getAllFriendsEmpty() {
    User userOne = addUser("test@yandex.ru", "TEST");

    assertThat(userStorage.getFriends(userOne.getId())).isEmpty();
  }

  @Test
  void getAllFriendsAfterAdd() {
    User userOne = addUser("test@yandex.ru", "TEST");
    User userTwo = addUser("test2@yandex.ru", "TEST2");

    userStorage.addFriend(userOne.getId(), userTwo.getId());

    assertThat(userStorage.getFriends(userOne.getId()))
        .extracting(User::getId)
        .containsExactly(userTwo.getId());
    assertThat(userStorage.getFriends(userTwo.getId())).isEmpty();
  }

  @Test
  void getCommonFriendsEmpty() {
    User userOne = addUser("test@yandex.ru", "TEST");
    User userTwo = addUser("test2@yandex.ru", "TEST2");

    assertThat(userStorage.getCommonFriends(userOne.getId(), userTwo.getId())).isEmpty();
  }

  @Test
  void getCommonFriendsWithOneCommon() {
    User userOne = addUser("test@yandex.ru", "TEST");
    User userTwo = addUser("test2@yandex.ru", "TEST2");
    User userThree = addUser("test3@yandex.ru", "TEST3");

    userStorage.addFriend(userOne.getId(), userThree.getId());
    userStorage.addFriend(userTwo.getId(), userThree.getId());

    assertThat(userStorage.getCommonFriends(userOne.getId(), userTwo.getId()))
        .extracting(User::getId)
        .containsExactly(userThree.getId());
  }
}
