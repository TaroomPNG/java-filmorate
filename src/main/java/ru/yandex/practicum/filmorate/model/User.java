package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.yandex.practicum.filmorate.model.dto.userDto.UserResponse;

@Data
@EqualsAndHashCode(of = {"email"})
@AllArgsConstructor
public class User {
  private Long id;
  private String email;
  private String login;
  private LocalDate birthday;
  private String name;
  private final Set<User> friendSet = new LinkedHashSet<>();

  public void addFriend(User user) {
    friendSet.add(user);
  }

  public void deleteFriend(User user) {
    friendSet.remove(user);
  }

  public boolean isFriendExist(User user) {
    return friendSet.contains(user);
  }

  public Set<UserResponse> getUserFriendResponse() {
    return this.friendSet.stream().map(UserResponse::new).collect(Collectors.toSet());
  }
}
