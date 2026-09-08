package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import java.util.Collection;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.controller.service.FeedService;
import ru.yandex.practicum.filmorate.controller.service.UserService;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.dto.feedDto.FeedResponse;
import ru.yandex.practicum.filmorate.model.dto.filmDto.FilmResponse;
import ru.yandex.practicum.filmorate.model.dto.userDto.UserPostRequest;
import ru.yandex.practicum.filmorate.model.dto.userDto.UserPutRequest;
import ru.yandex.practicum.filmorate.model.dto.userDto.UserResponse;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {

  private final UserService userService;
  private final FeedService feedService;

  @GetMapping
  public ResponseEntity<Collection<UserResponse>> getAllUsers() {
    return ResponseEntity.ok(userService.getAllUsers());
  }

  @GetMapping("/{id}")
  public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
    return ResponseEntity.ok(userService.getUserById(id));
  }

  @PostMapping
  public ResponseEntity<UserResponse> addUser(@Valid @RequestBody UserPostRequest userPostRequest) {
    return ResponseEntity.status(HttpStatus.CREATED).body(userService.addUser(userPostRequest));
  }

  @PutMapping
  public ResponseEntity<UserResponse> updateUser(@Valid @RequestBody UserPutRequest userPutRequest) {
    return ResponseEntity.ok(userService.updateUser(userPutRequest));
  }

  // FriendMapping

  @GetMapping("/{id}/friends")
  public ResponseEntity<Collection<UserResponse>> getAllUserFriends(@PathVariable Long id) {
    return ResponseEntity.ok(userService.getAllFriends(id));
  }

  @GetMapping("/{id}/friends/requests")
  public ResponseEntity<Collection<UserResponse>> getAllUserFriendsRequests(@PathVariable Long id) {
    return ResponseEntity.ok(userService.getAllFriendsRequest(id));
  }

  @GetMapping("/{id}/friends/common/{otherId}")
  public ResponseEntity<Collection<UserResponse>> getCommonFriends(
      @PathVariable Long id, @PathVariable Long otherId) {
    return ResponseEntity.ok(userService.getCommonFriends(id, otherId));
  }

  @PutMapping("/{id}/friends/{friendId}")
  public ResponseEntity<UserResponse> addFriend(@PathVariable Long id, @PathVariable Long friendId) {
    return ResponseEntity.ok(userService.addFriend(id, friendId));
  }

  @DeleteMapping("/{id}/friends/{friendId}")
  public ResponseEntity<UserResponse> deleteFriend(@PathVariable Long id, @PathVariable Long friendId) {
    return ResponseEntity.status(HttpStatus.NO_CONTENT).body(userService.deleteFriend(id, friendId));
  }

  @GetMapping("/{id}/feed")
  public ResponseEntity<List<FeedResponse>> getFeedByUser(@PathVariable Long id) {
    return ResponseEntity.ok(feedService.getFeedByUser(id));
  }

  @GetMapping("/{id}/feed/{type}")
  public ResponseEntity<List<FeedResponse>> getFeedByType(@PathVariable Long id, @PathVariable EventType type) {
    return ResponseEntity.ok(feedService.getFeedByEvent(id, type));
  }

  @GetMapping("/{id}/recommendations")
  public ResponseEntity<Collection<FilmResponse>> getRecommendations(@PathVariable Long id) {
    return ResponseEntity.ok(userService.getRecommendations(id));
  }



}
