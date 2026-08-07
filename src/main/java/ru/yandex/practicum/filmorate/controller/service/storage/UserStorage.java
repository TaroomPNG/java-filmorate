package ru.yandex.practicum.filmorate.controller.service.storage;

import java.util.List;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.dto.userDto.UserPostRequest;
import ru.yandex.practicum.filmorate.model.dto.userDto.UserPutRequest;

public interface UserStorage {
  User addUser(UserPostRequest userPostRequest);

  User updateUser(UserPutRequest userPutRequest);

  boolean deleteUser(long id);

  User getUserById(long id);

  List<User> getUsers();

  boolean isUserExists(long id);
}
