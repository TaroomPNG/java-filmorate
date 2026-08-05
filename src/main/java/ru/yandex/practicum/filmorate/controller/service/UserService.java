package ru.yandex.practicum.filmorate.controller.service;

import java.util.Collection;
import java.util.HashMap;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controller.exceptions.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.controller.exceptions.UserNotFound;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.dto.userDto.UserPostRequest;
import ru.yandex.practicum.filmorate.model.dto.userDto.UserPutRequest;
import ru.yandex.practicum.filmorate.model.dto.userDto.UserResponse;

@Slf4j
@Service
public class UserService {

  private final HashMap<Long, User> userHashMap = new HashMap<>();

  private static void updateUserBirthday(UserPutRequest userRequest, User oldUser) {
    log.trace("Вызывается метод updateUserBirthday");
    oldUser.setBirthday(userRequest.getBirthday());
    log.trace("Пользователь успешно обновил дату рождения на: {}", oldUser.getBirthday());
  }

  private static void updateUserName(UserPutRequest userRequest, User oldUser) {
    log.trace("Вызывается метод updateUserName");
    oldUser.setName(userRequest.getName());
    log.trace("Пользователь успешно обновил имя на: {}", oldUser.getName());
  }

  public void clearMap() {
    log.debug("Запрос на очистку userHashMap");
    userHashMap.clear();
  }

  public Collection<UserResponse> getAllUsers() {
    log.debug("Запрос getAllUsers");
    return userHashMap.values().stream()
        .map(
            u ->
                UserResponse.builder()
                    .id(u.getId())
                    .email(u.getEmail())
                    .login(u.getLogin())
                    .name(u.getName())
                    .birthday(u.getBirthday())
                    .build())
        .collect(Collectors.toList());
  }

  public UserResponse addUser(UserPostRequest userRequest) {
    log.debug("Запрос addUser: {}", userRequest);
    boolean existsEmail =
        userHashMap.values().stream()
            .anyMatch(user -> user.getEmail().equalsIgnoreCase(userRequest.getEmail()));

    if (existsEmail) {
      log.warn("Ошибка валидации: email {} уже занят", userRequest.getEmail());
      throw new ConditionsNotMetException("Такой Email уже занят");
    }

    boolean existsLogin =
        userHashMap.values().stream()
            .anyMatch(user -> user.getLogin().equalsIgnoreCase(userRequest.getLogin()));

    if (existsLogin) {
      log.warn("Ошибка валидации: login {} уже занят", userRequest.getLogin());
      throw new ConditionsNotMetException("Такой Login уже занят");
    }

    User newUser =
        new User(
            generateId(),
            userRequest.getEmail(),
            userRequest.getLogin(),
            userRequest.getBirthday(),
            userRequest.getName());
    log.debug("Сформирован объект {}", newUser);

    if (newUser.getName() == null || newUser.getName().isBlank()) {
      newUser.setName(userRequest.getLogin());
    }

    log.info("Объект {} успешно добавлен по ID - {}", newUser.getLogin(), newUser.getId());

    userHashMap.put(newUser.getId(), newUser);

    return UserResponse.builder()
        .id(newUser.getId())
        .email(newUser.getEmail())
        .login(newUser.getLogin())
        .name(newUser.getName())
        .birthday(newUser.getBirthday())
        .build();
  }

  public UserResponse updateUser(UserPutRequest userRequest) {
    log.debug("Запрос updateUser: {}", userRequest);

    User oldUser = userHashMap.get(userRequest.getId());

    if (oldUser == null) {
      log.warn("Ошибка валидации: пользователь по ID - {} не найден", userRequest.getId());
      throw new UserNotFound(String.format("По ID %d пользователь не найден", userRequest.getId()));
    }

    if (userRequest.getEmail() != null) {
      log.debug("Обнаружено поле Email");
      updateUserEmail(userRequest, oldUser);
    }

    if (userRequest.getLogin() != null) {
      log.debug("Обнаружено поле Login");
      updateUserLogin(userRequest, oldUser);
    }

    if (userRequest.getName() != null) {
      log.debug("Обнаружено поле Name");
      updateUserName(userRequest, oldUser);
    }

    if (userRequest.getBirthday() != null) {
      log.debug("Обнаружено поле Birthday");
      updateUserBirthday(userRequest, oldUser);
    }

    log.debug("Объект успешно обновлен {}", userRequest);

    return UserResponse.builder()
        .id(oldUser.getId())
        .email(oldUser.getEmail())
        .login(oldUser.getLogin())
        .name(oldUser.getName())
        .birthday(oldUser.getBirthday())
        .build();
  }

  private void updateUserLogin(UserPutRequest userRequest, User oldUser) {
    log.trace("Вызывается метод updateUserLogin");
    boolean existsLogin =
        userHashMap.values().stream()
            .anyMatch(user -> user.getLogin().equalsIgnoreCase(userRequest.getLogin()));

    if (existsLogin) {
      log.warn("Ошибка валидации: login {} занят для обновления", userRequest.getLogin());
      throw new ConditionsNotMetException("Такой Login уже занят");
    }

    oldUser.setLogin(userRequest.getLogin());
    log.trace("Пользователь успешно обновил логин на: {}", oldUser.getLogin());
  }

  private void updateUserEmail(UserPutRequest userRequest, User oldUser) {
    log.trace("Вызывается метод updateUserEmail");
    boolean existsEmail =
        userHashMap.values().stream()
            .anyMatch(user -> user.getEmail().equalsIgnoreCase(userRequest.getEmail()));

    if (existsEmail) {
      log.warn("Ошибка валидации: email {} занят для обновления", userRequest.getEmail());
      throw new ConditionsNotMetException("Такой Email уже занят");
    }

    oldUser.setEmail(userRequest.getEmail());
    log.trace("Пользователь успешно обновил email на: {}", oldUser.getEmail());
  }

  private Long generateId() {
    long currentMaxId = userHashMap.values().stream().mapToLong(User::getId).max().orElse(0);

    return ++currentMaxId;
  }
}
