package ru.yandex.practicum.filmorate.controller.service;

import java.util.Collection;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controller.exceptions.UserNotFound;
import ru.yandex.practicum.filmorate.controller.service.storage.UserStorage;
import ru.yandex.practicum.filmorate.model.dto.userDto.UserPostRequest;
import ru.yandex.practicum.filmorate.model.dto.userDto.UserPutRequest;
import ru.yandex.practicum.filmorate.model.dto.userDto.UserResponse;

@Slf4j
@Service
public class UserService {

  @Autowired
  @Qualifier("UserDbStorage")
  UserStorage userStorage;

  public UserResponse addFriend(Long userId, Long friendId) {
    log.trace("Вызывается addFriend: UserID {} - FriendID {}", userId, friendId);
    if (!userStorage.isUserExists(userId)) {
      throw new UserNotFound(userId);
    }
    if (!userStorage.isUserExists(friendId)) {
      throw new UserNotFound(friendId);
    }
    userStorage.addFriend(userId, friendId);
    return new UserResponse(userStorage.getUserById(userId));
  }

  public UserResponse deleteFriend(Long userId, Long friendId) {
    log.trace("Вызывается deleteFriend: UserID {} - FriendID {}", userId, friendId);
    if (!userStorage.isUserExists(userId)) {
      throw new UserNotFound(userId);
    }
    if (!userStorage.isUserExists(friendId)) {
      throw new UserNotFound(friendId);
    }
    userStorage.removeFriend(userId, friendId);
    return new UserResponse(userStorage.getUserById(userId));
  }

  public List<UserResponse> getAllFriendsRequest(Long userId) {
    return getAllFriends(userId);
  }

  public List<UserResponse> getAllFriends(Long userId) {
    log.trace("Вызывается getAllFriends: UserID {}", userId);
    if (!userStorage.isUserExists(userId)) {
      throw new UserNotFound(userId);
    }
    return userStorage.getFriends(userId).stream().map(UserResponse::new).toList();
  }

  public List<UserResponse> getCommonFriends(Long userId, Long otherId) {
    log.trace("Вызывается getCommonFriends: UserID {} - OtherID {}", userId, otherId);
    if (!userStorage.isUserExists(userId)) {
      throw new UserNotFound(userId);
    }
    if (!userStorage.isUserExists(otherId)) {
      throw new UserNotFound(otherId);
    }
    return userStorage.getCommonFriends(userId, otherId).stream().map(UserResponse::new).toList();
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
}
