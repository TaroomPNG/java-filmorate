package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
@EqualsAndHashCode(of = {"id", "eventType", "entityId"})
public class Feed {
  private Long id;
  private Long timestamp;
  private Long userId;
  private EventType eventType;
  private Operation operation;
  private Long entityId;
}
