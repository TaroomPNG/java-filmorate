package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import java.util.Collection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.controller.service.UserService;
import ru.yandex.practicum.filmorate.model.dto.userDto.UserPostRequest;
import ru.yandex.practicum.filmorate.model.dto.userDto.UserPutRequest;
import ru.yandex.practicum.filmorate.model.dto.userDto.UserResponse;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {

  @Autowired @Getter private final UserService userService;

  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  public Collection<UserResponse> getAllUsers() {
    return userService.getAllUsers();
  }

  @GetMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public UserResponse getUserById(@PathVariable Long id) {
    return userService.getUserById(id);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public UserResponse addUser(@Valid @RequestBody UserPostRequest userPostRequest) {
    return userService.addUser(userPostRequest);
  }

  @PutMapping
  @ResponseStatus(HttpStatus.OK)
  public UserResponse updateUser(@Valid @RequestBody UserPutRequest userPutRequest) {
    return userService.updateUser(userPutRequest);
  }

  // FriendMapping

  @GetMapping("/{id}/friends")
  @ResponseStatus(HttpStatus.OK)
  public Collection<UserResponse> getAllUserFriends(@PathVariable Long id) {
    return userService.getAllFriends(id);
  }

  @GetMapping("/{id}/friends/common/{otherId}")
  @ResponseStatus(HttpStatus.OK)
  public Collection<UserResponse> getCommonFriends(
      @PathVariable Long id, @PathVariable Long otherId) {
    return userService.getCommonFriends(id, otherId);
  }

  @PutMapping("/{id}/friends/{friendId}")
  @ResponseStatus(HttpStatus.OK)
  public UserResponse addFriend(@PathVariable Long id, @PathVariable Long friendId) {
    return userService.addFriend(id, friendId);
  }

  @DeleteMapping("/{id}/friends/{friendId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public UserResponse deleteFriend(@PathVariable Long id, @PathVariable Long friendId) {
    return userService.deleteFriend(id, friendId);
  }
}
