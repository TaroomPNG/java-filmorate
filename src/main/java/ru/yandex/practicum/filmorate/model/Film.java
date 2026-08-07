package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.*;
import ru.yandex.practicum.filmorate.model.dto.userDto.UserResponse;

/** Film. */
@Data
@EqualsAndHashCode(of = {"name"})
@AllArgsConstructor
public class Film {
  private Long id;
  private String name;
  private String description;
  private LocalDate releaseDate;
  private Integer duration;
  private final Set<User> likeIds = new LinkedHashSet<>();

  public void addLike(User user) {
    likeIds.add(user);
  }

  public void deleteLike(User user) {
    likeIds.remove(user);
  }

  public boolean isLikeExists(User user) {
    return likeIds.contains(user);
  }

  public Set<UserResponse> getUserLikeResponse() {
    return this.likeIds.stream()
        .map(
            user ->
                UserResponse.builder()
                    .id(user.getId())
                    .email(user.getEmail())
                    .login(user.getLogin())
                    .name(user.getName())
                    .birthday(user.getBirthday())
                    .build())
        .collect(Collectors.toSet());
  }

  public int getLikeCount() {
    return likeIds.size();
  }
}
