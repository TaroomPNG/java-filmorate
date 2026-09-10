package ru.yandex.practicum.filmorate.controller.service;

import java.util.Collection;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controller.exceptions.UserNotFound;
import ru.yandex.practicum.filmorate.controller.service.storage.FilmStorage;
import ru.yandex.practicum.filmorate.controller.service.storage.ReviewStorage;
import ru.yandex.practicum.filmorate.controller.service.storage.UserStorage;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.model.dto.feedDto.FeedPostRequest;
import ru.yandex.practicum.filmorate.model.dto.filmDto.FilmResponse;
import ru.yandex.practicum.filmorate.model.dto.userDto.UserPostRequest;
import ru.yandex.practicum.filmorate.model.dto.userDto.UserPutRequest;
import ru.yandex.practicum.filmorate.model.dto.userDto.UserResponse;

@Slf4j
@Service
public class UserService {

  @Autowired
  @Qualifier("UserDbStorage")
  UserStorage userStorage;

    @Autowired
    @Qualifier("FilmDbStorage")
    FilmStorage filmStorage;

  @Autowired private FeedService feedService;

  @Autowired
  @Qualifier("ReviewDbStorage")
  ReviewStorage reviewStorage;

  public UserResponse addFriend(Long userId, Long friendId) {
    log.trace("Вызывается addFriend: UserID {} - FriendID {}", userId, friendId);
    if (!userStorage.isUserExists(userId)) {
      throw new UserNotFound(userId);
    }
    if (!userStorage.isUserExists(friendId)) {
      throw new UserNotFound(friendId);
    }
    userStorage.addFriend(userId, friendId);
    feedService.addToFeed(new FeedPostRequest(userId, EventType.FRIEND, Operation.ADD, friendId));

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
    feedService.addToFeed(
        new FeedPostRequest(userId, EventType.FRIEND, Operation.REMOVE, friendId));

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

  public boolean deleteUser(Long id) {
    log.trace("Запрос UserResponse через deleteUser");
    reviewStorage.deleteByUserId(id);
    return userStorage.deleteUser(id);
  }

  public List<FilmResponse> getRecommendations(Long id) {
    if (!userStorage.isUserExists(id)) {
        throw new UserNotFound(id);
    }
    return filmStorage.getRecommendation(id).stream()
                .map(FilmResponse::new)
                .toList();
  }
}
