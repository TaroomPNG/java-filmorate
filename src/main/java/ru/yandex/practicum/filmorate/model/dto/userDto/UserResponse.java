package ru.yandex.practicum.filmorate.model.dto.userDto;

import java.time.LocalDate;
import java.util.Set;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.User;

@Data
@Builder
public class UserResponse {
  private Long id;
  private String email;
  private String login;
  private String name;
  private LocalDate birthday;
  private final Set<UserResponse> friendSet;

  public UserResponse(User user) {
    this.id = user.getId();
    this.email = user.getEmail();
    this.login = user.getLogin();
    this.name = user.getName();
    this.birthday = user.getBirthday();
    this.friendSet = user.getUserFriendResponse();
  }
}
