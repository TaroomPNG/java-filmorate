package ru.yandex.practicum.filmorate.controller.service;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controller.exceptions.DuplicationExceptions;
import ru.yandex.practicum.filmorate.controller.exceptions.UserNotFound;
import ru.yandex.practicum.filmorate.controller.service.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.model.FriendStatus;
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

    if (friend.isFriendExist(userId) && friend.isFriendUnconfirmed(userId)) {
      log.debug(
          "У пользователя {} существует неподтвержденная заявка в друзья к {}",
          friend.getLogin(),
          user.getLogin());
      user.setFriendStatus(friendId, FriendStatus.CONFIRMED);
      friend.setFriendStatus(userId, FriendStatus.CONFIRMED);

      log.info("Пользователь {} добавил в друзья {}", user.getLogin(), friend.getLogin());
    } else if (user.isFriendExist(friendId)) {
      throw new DuplicationExceptions(
          String.format(
              "Данный ID - %d уже внесен в друзья со статусом ID - %s",
              friendId, user.getFriendStatus(friendId)));
    } else {
      log.debug("Ранее составленной заявки не обнаружено - добавляем UNCONFIRMED");
      user.setFriendStatus(friendId, FriendStatus.UNCONFIRMED);
    }
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

    if (user.isFriendExist(friendId)) {
      log.debug("У {} обнаружены записи дружбы с {}", friend.getLogin(), user.getLogin());
      user.deleteFriend(friendId);
      friend.deleteFriend(userId);
      log.debug("Записи удалены");
    }

    log.info("Пользователь {} удалил из друзей {}", user.getLogin(), friend.getLogin());

    return new UserResponse(user);
  }

  public List<UserResponse> getAllFriendsRequest(Long userId) {
    log.trace("Вызывается getAllFriendsRequest: UserID {}", userId);

    if (!userStorage.isUserExists(userId)) {
      throw new UserNotFound(userId);
    }

    User user = userStorage.getUserById(userId);

    log.debug(
        "Найден пользователь {} с заявками: {}", user, user.getAllUnconfirmedFriendsId().size());

    return user.getAllUnconfirmedFriendsId().stream()
        .map(userStorage::getUserById)
        .map(UserResponse::new)
        .toList();
  }

  public List<UserResponse> getAllFriends(Long userId) {
    log.trace("Вызывается getAllFriends: UserID {}", userId);

    if (!userStorage.isUserExists(userId)) {
      throw new UserNotFound(userId);
    }

    User user = userStorage.getUserById(userId);

    log.debug(
        "Найден пользователь {} с друзьями: {}", user, user.getAllConfirmedFriendsId().size());

    return user.getAllConfirmedFriendsId().stream()
        .map(userStorage::getUserById)
        .map(UserResponse::new)
        .toList();
  }

  public List<UserResponse> getCommonFriends(Long userId, Long otherId) {
    log.trace("Вызывается getCommonFriends: UserID {} - OtherID {}", userId, otherId);

    if (!userStorage.isUserExists(userId)) {
      throw new UserNotFound(userId);
    }

    if (!userStorage.isUserExists(otherId)) {
      throw new UserNotFound(otherId);
    }

    Set<Long> userFriendIds = userStorage.getUserById(userId).getAllConfirmedFriendsId();
    Set<Long> otherFriendIds = userStorage.getUserById(otherId).getAllConfirmedFriendsId();

    log.debug(
        "Списки друзей для поиска общих: userFriends={}, otherFriends={}",
        userFriendIds.size(),
        otherFriendIds.size());

    return userFriendIds.stream()
        .filter(otherFriendIds::contains)
        .map(userStorage::getUserById)
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
