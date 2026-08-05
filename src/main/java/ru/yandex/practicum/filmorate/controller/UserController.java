package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import java.util.Collection;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.controller.service.UserService;
import ru.yandex.practicum.filmorate.model.dto.userDto.UserPostRequest;
import ru.yandex.practicum.filmorate.model.dto.userDto.UserPutRequest;
import ru.yandex.practicum.filmorate.model.dto.userDto.UserResponse;

@RestController
@RequestMapping("/users")
public class UserController {

  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping
  public Collection<UserResponse> getAllUsers() {
    return userService.getAllUsers();
  }

  @PostMapping
  public UserResponse addUser(@Valid @RequestBody UserPostRequest userPostRequest) {
    return userService.addUser(userPostRequest);
  }

  @PutMapping
  public UserResponse updateUser(@Valid @RequestBody UserPutRequest userPutRequest) {
    return userService.updateUser(userPutRequest);
  }
}
