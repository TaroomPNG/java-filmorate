package ru.yandex.practicum.filmorate.controller.service.storage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.controller.exceptions.UserNotFound;
import ru.yandex.practicum.filmorate.model.FriendStatus;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.dto.userDto.UserPostRequest;
import ru.yandex.practicum.filmorate.model.dto.userDto.UserPutRequest;

@Repository("UserDbStorage")
public class UserDbStorage extends BaseRepository<User> implements UserStorage {

  private static final String FIND_BY_ID_USER = "SELECT * FROM \"user\" WHERE user_id = ?";
  private static final String FIND_ALL_USERS = "SELECT * FROM \"user\" ORDER BY user_id";
  private static final String DELETE_FRIENDSHIPS =
      "DELETE FROM user_friendship WHERE user_id = ? OR friend_id = ?";
  private static final String DELETE_LIKES = "DELETE FROM film_likes WHERE user_id = ?";
  private static final String DELETE_REVIEW_REACTIONS_BY_USER =
      "DELETE FROM review_reaction WHERE user_id = ?";
  private static final String DELETE_REVIEW_REACTIONS_ON_USER_REVIEWS =
      "DELETE FROM review_reaction WHERE review_id IN (SELECT review_id FROM review WHERE user_id = ?)";
  private static final String DELETE_REVIEWS_BY_USER = "DELETE FROM review WHERE user_id = ?";
  private static final String DELETE_BY_ID_USER = "DELETE FROM \"user\" WHERE user_id = ?";
  private static final String ADD_NEW_USER =
      "INSERT INTO \"user\" (email, login, birthday, name) VALUES (?, ?, ?, ?)";
  private static final String ADD_FRIEND =
      "MERGE INTO user_friendship (user_id, friend_id, status_id) KEY (user_id, friend_id) "
          + "VALUES (?, ?, (SELECT id FROM friendship_status WHERE status = 'CONFIRMED'))";
  private static final String DELETE_FRIEND =
      "DELETE FROM user_friendship WHERE user_id = ? AND friend_id = ?";
  private static final String IS_FRIEND =
      "SELECT COUNT(*) FROM user_friendship WHERE user_id = ? AND friend_id = ?";
  private static final String FIND_FRIENDS =
      "SELECT u.* FROM \"user\" AS u "
          + "JOIN user_friendship AS uf ON u.user_id = uf.friend_id "
          + "WHERE uf.user_id = ? ORDER BY u.user_id";
  private static final String FIND_COMMON_FRIENDS =
      "SELECT u.* FROM \"user\" AS u "
          + "JOIN user_friendship AS uf1 ON u.user_id = uf1.friend_id AND uf1.user_id = ? "
          + "JOIN user_friendship AS uf2 ON u.user_id = uf2.friend_id AND uf2.user_id = ? "
          + "ORDER BY u.user_id";
  private static final String FIND_FRIEND_IDS =
      "SELECT friend_id FROM user_friendship WHERE user_id = ?";

  public UserDbStorage(JdbcTemplate jdbc, @Qualifier("userRowMapper") RowMapper<User> mapper) {
    super(jdbc, mapper);
  }

  @Override
  public User addUser(UserPostRequest userPostRequest) {
    String name = userPostRequest.getName();
    if (name == null || name.isBlank()) {
      name = userPostRequest.getLogin();
    }
    long id =
        insert(
            ADD_NEW_USER,
            userPostRequest.getEmail(),
            userPostRequest.getLogin(),
            userPostRequest.getBirthday(),
            name);
    return getUserById(id);
  }

  @Override
  public User updateUser(UserPutRequest userPutRequest) {
    if (!isUserExists(userPutRequest.getId())) {
      throw new UserNotFound(userPutRequest.getId());
    }

    List<String> setClauses = new ArrayList<>();
    List<Object> params = new ArrayList<>();

    if (userPutRequest.getEmail() != null) {
      setClauses.add("email = ?");
      params.add(userPutRequest.getEmail());
    }
    if (userPutRequest.getLogin() != null) {
      setClauses.add("login = ?");
      params.add(userPutRequest.getLogin());
    }
    if (userPutRequest.getBirthday() != null) {
      setClauses.add("birthday = ?");
      params.add(userPutRequest.getBirthday());
    }
    if (userPutRequest.getName() != null) {
      setClauses.add("name = ?");
      params.add(userPutRequest.getName());
    }

    if (!setClauses.isEmpty()) {
      String updateQuery =
          "UPDATE \"user\" SET " + String.join(", ", setClauses) + " WHERE user_id = ?";
      params.add(userPutRequest.getId());
      update(updateQuery, params.toArray());
    }

    return getUserById(userPutRequest.getId());
  }

  @Override
  public boolean deleteUser(long id) {
    jdbc.update(DELETE_FRIENDSHIPS, id, id);
    jdbc.update(DELETE_LIKES, id);
    jdbc.update(DELETE_REVIEW_REACTIONS_BY_USER, id);
    jdbc.update(DELETE_REVIEW_REACTIONS_ON_USER_REVIEWS, id);
    jdbc.update(DELETE_REVIEWS_BY_USER, id);
    return delete(DELETE_BY_ID_USER, id);
  }

  @Override
  public User getUserById(long id) {
    Optional<User> optionalUser = findOne(FIND_BY_ID_USER, id);
    if (optionalUser.isEmpty()) {
      throw new UserNotFound(id);
    }
    User user = optionalUser.get();
    loadFriends(user);
    return user;
  }

  @Override
  public List<User> getUsers() {
    List<User> users = List.copyOf(findMany(FIND_ALL_USERS));
    users.forEach(this::loadFriends);
    return users;
  }

  @Override
  public boolean isUserExists(long id) {
    return findOne(FIND_BY_ID_USER, id).isPresent();
  }

  @Override
  public void addFriend(long userId, long friendId) {
    jdbc.update(ADD_FRIEND, userId, friendId);
  }

  @Override
  public void removeFriend(long userId, long friendId) {
    jdbc.update(DELETE_FRIEND, userId, friendId);
  }

  @Override
  public boolean isFriend(long userId, long friendId) {
    Integer count = jdbc.queryForObject(IS_FRIEND, Integer.class, userId, friendId);
    return count != null && count > 0;
  }

  @Override
  public List<User> getFriends(long userId) {
    return jdbc.query(FIND_FRIENDS, mapper, userId);
  }

  @Override
  public List<User> getCommonFriends(long userId, long otherId) {
    return jdbc.query(FIND_COMMON_FRIENDS, mapper, userId, otherId);
  }

  @Override
  public void clear() {
    jdbc.update("DELETE FROM user_friendship");
    jdbc.update("DELETE FROM review_reaction");
    jdbc.update("DELETE FROM review");
    jdbc.update("DELETE FROM film_likes");
    jdbc.update("DELETE FROM \"user\"");
    jdbc.update("ALTER TABLE \"user\" ALTER COLUMN user_id RESTART WITH 1");
  }

  private void loadFriends(User user) {
    List<Long> friendIds = jdbc.queryForList(FIND_FRIEND_IDS, Long.class, user.getId());
    friendIds.forEach(friendId -> user.setFriendStatus(friendId, FriendStatus.CONFIRMED));
  }
}
