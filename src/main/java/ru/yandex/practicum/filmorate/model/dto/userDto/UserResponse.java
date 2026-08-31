package ru.yandex.practicum.filmorate.model.dto.userDto;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;
import ru.yandex.practicum.filmorate.model.User;

@Data
@Builder
@AllArgsConstructor
@Jacksonized
public class UserResponse {
  private Long id;
  private String email;
  private String login;
  private String name;
  private LocalDate birthday;
  @Builder.Default private final Set<Long> friendIdSet = new LinkedHashSet<>();

  public UserResponse(User user) {
    this.id = user.getId();
    this.email = user.getEmail();
    this.login = user.getLogin();
    this.name = user.getName();
    this.birthday = user.getBirthday();
    this.friendIdSet = user.getAllConfirmedFriendsId();
  }
}
