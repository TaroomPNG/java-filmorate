package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@AllArgsConstructor
@ToString
@Data
@Builder
public class Friendship {
  private final Long userId;
  private final Long friendId;
  private final FriendStatus status;
}
