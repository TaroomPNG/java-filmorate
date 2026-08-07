package ru.yandex.practicum.filmorate.controller.service;

import java.util.Collection;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controller.exceptions.UserNotFound;
import ru.yandex.practicum.filmorate.controller.service.storage.InMemoryFilmStorage;
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
  @Autowired InMemoryFilmStorage filmStorage;

  public UserResponse addFriend(Long userId, Long friendId) {
    if (!userStorage.isUserExists(userId)) {
      throw new UserNotFound(userId);
    }

    if (!userStorage.isUserExists(friendId)) {
      throw new UserNotFound(friendId);
    }

    User user = userStorage.getUserById(userId);
    User friend = userStorage.getUserById(friendId);

    user.addFriend(friend);
    friend.addFriend(user);

    return new UserResponse(user);
  }

  public UserResponse deleteFriend(Long userId, Long friendId) {
    if (!userStorage.isUserExists(userId)) {
      throw new UserNotFound(userId);
    }

    if (!userStorage.isUserExists(friendId)) {
      throw new UserNotFound(friendId);
    }

    User user = userStorage.getUserById(userId);
    User friend = userStorage.getUserById(friendId);

    user.deleteFriend(friend);
    friend.deleteFriend(user);

    return new UserResponse(user);
  }

  public List<UserResponse> getAllFriends(Long userId) {
    if (!userStorage.isUserExists(userId)) {
      throw new UserNotFound(userId);
    }

    User user = userStorage.getUserById(userId);

    return user.getFriendSet().stream().map(UserResponse::new).toList();
  }

  public List<UserResponse> getCommonFriends(Long userId, Long otherId) {
    if (!userStorage.isUserExists(userId)) {
      throw new UserNotFound(userId);
    }

    if (!userStorage.isUserExists(otherId)) {
      throw new UserNotFound(otherId);
    }

    List<User> userFriendList = userStorage.getUserById(userId).getFriendSet().stream().toList();
    List<User> otherFriendList = userStorage.getUserById(otherId).getFriendSet().stream().toList();

    return userFriendList.stream()
        .filter(otherFriendList::contains)
        .map(UserResponse::new)
        .toList();
  }

  public UserResponse getUserById(Long id) {
    return new UserResponse(userStorage.getUserById(id));
  }

  public Collection<UserResponse> getAllUsers() {
    return userStorage.getUsers().stream().map(UserResponse::new).toList();
  }

  public UserResponse addUser(UserPostRequest userPostRequest) {
    return new UserResponse(userStorage.addUser(userPostRequest));
  }

  public UserResponse updateUser(UserPutRequest userPutRequest) {
    return new UserResponse(userStorage.updateUser(userPutRequest));
  }
}
