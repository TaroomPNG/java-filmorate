package ru.yandex.practicum.filmorate.model.dto.feedDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;

@Data
@Builder
@AllArgsConstructor
public class FeedPostRequest {
  private Long timestamp;
  private Long userId;
  private EventType eventType;
  private Operation operation;
  private Long entityId;

  public FeedPostRequest(Long userId, EventType type, Operation operation, Long entityId) {
    this.timestamp = System.currentTimeMillis();
    this.userId = userId;
    this.eventType = type;
    this.operation = operation;
    this.entityId = entityId;
  }
}
