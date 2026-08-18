package ru.yandex.practicum.filmorate.controller.service;

import java.util.Collection;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controller.exceptions.UserNotFound;
import ru.yandex.practicum.filmorate.controller.service.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.dto.userDto.UserPostRequest;
import ru.yandex.practicum.filmorate.model.dto.userDto.UserPutRequest;
import ru.yandex.practicum.filmorate.model.dto.userDto.UserResponse;

@Slf4j
@Service
@AllArgsConstructor
public class UserService {

  @Autowired InMemoryUserStorage userStorage;

  public UserResponse addFriend(Long userId, Long friendId) {
    log.trace("Вызывается addFriend: UserID {} - FriendID {}", userId, friendId);

    if (!userStorage.isUserExists(userId)) {
      throw new UserNotFound(userId);
    }

    if (!userStorage.isUserExists(friendId)) {
      throw new UserNotFound(friendId);
    }

    User user = userStorage.getUserById(userId);
    User friend = userStorage.getUserById(friendId);

    log.debug(
        "Найдено два объекта для добавления в друзья: \n User - {} \n Friend - {}", user, friend);

    user.addFriend(friend);
    friend.addFriend(user);

    log.info("Пользователь {} добавил в друзья {}", user.getLogin(), friend.getLogin());

    return new UserResponse(user);
  }

  public UserResponse deleteFriend(Long userId, Long friendId) {
    log.trace("Вызывается deleteFriend: UserID {} - FriendID {}", userId, friendId);

    if (!userStorage.isUserExists(userId)) {
      throw new UserNotFound(userId);
    }

    if (!userStorage.isUserExists(friendId)) {
      throw new UserNotFound(friendId);
    }

    User user = userStorage.getUserById(userId);
    User friend = userStorage.getUserById(friendId);

    log.debug(
        "Найдено два объекта для удаления из друзей: \n User - {} \n Friend - {}", user, friend);

    user.deleteFriend(friend);
    friend.deleteFriend(user);

    log.info("Пользователь {} удалил из друзей {}", user.getLogin(), friend.getLogin());

    return new UserResponse(user);
  }

  public List<UserResponse> getAllFriends(Long userId) {
    log.trace("Вызывается getAllFriends: UserID {}", userId);

    if (!userStorage.isUserExists(userId)) {
      throw new UserNotFound(userId);
    }

    User user = userStorage.getUserById(userId);

    log.debug("Найден пользователь с {} друзьями: {}", user.getFriendSet().size(), user);

    return user.getFriendSet().stream().map(UserResponse::new).toList();
  }

  public List<UserResponse> getCommonFriends(Long userId, Long otherId) {
    log.trace("Вызывается getCommonFriends: UserID {} - OtherID {}", userId, otherId);

    if (!userStorage.isUserExists(userId)) {
      throw new UserNotFound(userId);
    }

    if (!userStorage.isUserExists(otherId)) {
      throw new UserNotFound(otherId);
    }

    List<User> userFriendList = userStorage.getUserById(userId).getFriendSet().stream().toList();
    List<User> otherFriendList = userStorage.getUserById(otherId).getFriendSet().stream().toList();

    log.debug(
        "Списки друзей для поиска общих: userFriends={}, otherFriends={}",
        userFriendList.size(),
        otherFriendList.size());

    return userFriendList.stream()
        .filter(otherFriendList::contains)
        .map(UserResponse::new)
        .toList();
  }

  public UserResponse getUserById(Long id) {
    log.trace("Запрос UserResponse через getUserById");
    return new UserResponse(userStorage.getUserById(id));
  }

  public Collection<UserResponse> getAllUsers() {
    log.trace("Запрос UserResponse через getAllUsers");
    return userStorage.getUsers().stream().map(UserResponse::new).toList();
  }

  public UserResponse addUser(UserPostRequest userPostRequest) {
    log.trace("Запрос UserResponse через addUser");
    return new UserResponse(userStorage.addUser(userPostRequest));
  }

  public UserResponse updateUser(UserPutRequest userPutRequest) {
    log.trace("Запрос UserResponse через updateUser");
    return new UserResponse(userStorage.updateUser(userPutRequest));
  }

  public void clearMap() {
    log.trace("Запрос на очистку через clearMap");
    userStorage.clearMap();
  }
}
