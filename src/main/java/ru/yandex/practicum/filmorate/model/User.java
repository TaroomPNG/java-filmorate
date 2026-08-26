package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = {"email"})
@AllArgsConstructor
public class User {
  private Long id;
  private String email;
  private String login;
  private LocalDate birthday;
  private String name;
  private final HashMap<Long, FriendStatus> friends = new HashMap<>();

  public void setFriendStatus(Long friendId, FriendStatus friendStatus) {
    friends.put(friendId, friendStatus);
  }

  public void deleteFriend(Long friendId) {
    friends.remove(friendId);
  }

  public boolean isFriendExist(Long friendId) {
    return friends.containsKey(friendId);
  }

  public FriendStatus getFriendStatus(Long friendId) {
    return friends.get(friendId);
  }

  public boolean isFriendUnconfirmed(Long friendId) {
    return friends.entrySet().stream()
        .filter(entry -> Objects.equals(entry.getKey(), friendId))
        .anyMatch(entry -> entry.getValue().equals(FriendStatus.UNCONFIRMED));
  }

  public Set<Long> getAllConfirmedFriendsId() {
    return this.friends.entrySet().stream()
        .filter(entry -> entry.getValue().equals(FriendStatus.CONFIRMED))
        .map(Map.Entry::getKey)
        .collect(Collectors.toSet());
  }

  public Set<Long> getAllUnconfirmedFriendsId() {
    return this.friends.entrySet().stream()
        .filter(entry -> entry.getValue().equals(FriendStatus.UNCONFIRMED))
        .map(Map.Entry::getKey)
        .collect(Collectors.toSet());
  }
}
