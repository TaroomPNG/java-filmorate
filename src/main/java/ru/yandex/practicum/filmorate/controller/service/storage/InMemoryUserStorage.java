package ru.yandex.practicum.filmorate.controller.service.storage;

import java.util.HashMap;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.controller.exceptions.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.controller.exceptions.FilmNotFound;
import ru.yandex.practicum.filmorate.controller.exceptions.UserNotFound;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.dto.userDto.UserPostRequest;
import ru.yandex.practicum.filmorate.model.dto.userDto.UserPutRequest;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
  private final HashMap<Long, User> userHashMap = new HashMap<>();

  private static void updateUserBirthday(UserPutRequest userRequest, User oldUser) {
    log.trace("Вызывается метод updateUserBirthday");
    oldUser.setBirthday(userRequest.getBirthday());
    log.debug("Пользователь успешно обновил дату рождения на: {}", oldUser.getBirthday());
  }

  private static void updateUserName(UserPutRequest userRequest, User oldUser) {
    log.trace("Вызывается метод updateUserName");
    oldUser.setName(userRequest.getName());
    log.debug("Пользователь успешно обновил имя на: {}", oldUser.getName());
  }

  @Override
  public boolean isUserExists(long id) {
    log.trace("Запрос isUserExists");
    return userHashMap.containsKey(id);
  }

  public void clearMap() {
    log.trace("Запрос на очистку userHashMap");
    userHashMap.clear();
  }

  @Override
  public User addUser(UserPostRequest userRequest) {
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

    return newUser;
  }

  @Override
  public User updateUser(UserPutRequest userRequest) {
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

    log.info("Обновленный объект User - {}", oldUser);

    return oldUser;
  }

  @Override
  public boolean deleteUser(long id) {
    log.trace("Запрос deleteUser");
    if (!userHashMap.containsKey(id)) {
      log.warn("Ошибка валидации: пользователь по ID - {} не найден", id);
      throw new FilmNotFound(String.format("По ID %d user не найден", id));
    }
    userHashMap.remove(id);
    log.debug("User по ID {} успешно удален", id);
    return true;
  }

  @Override
  public User getUserById(long id) {
    log.trace("Запрос getUserById");
    if (!userHashMap.containsKey(id)) {
      log.warn("Ошибка валидации: пользователь по ID - {} не найден", id);
      throw new ConditionsNotMetException("Фильм по объекту не найден");
    }

    return userHashMap.get(id);
  }

  @Override
  public List<User> getUsers() {
    log.trace("Запрос getAllUsers");
    return userHashMap.values().stream().toList();
  }

  private Long generateId() {
    long currentMaxId = userHashMap.values().stream().mapToLong(User::getId).max().orElse(0);

    log.info("Сгенерирован ID - {}", currentMaxId);
    return ++currentMaxId;
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
    log.debug("Пользователь успешно обновил логин на: {}", oldUser.getLogin());
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
    log.debug("Пользователь успешно обновил email на: {}", oldUser.getEmail());
  }
}
