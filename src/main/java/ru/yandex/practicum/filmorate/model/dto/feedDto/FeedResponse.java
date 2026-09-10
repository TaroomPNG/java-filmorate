package ru.yandex.practicum.filmorate.model.dto.feedDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Operation;

@Data
@AllArgsConstructor
@Builder
@Jacksonized
public class FeedResponse {
  private Long eventId;
  private Long timestamp;
  private Long userId;
  private EventType eventType;
  private Operation operation;
  private Long entityId;

  public FeedResponse(Feed feed) {
    this.eventId = feed.getId();
    this.timestamp = feed.getTimestamp();
    this.userId = feed.getUserId();
    this.eventType = feed.getEventType();
    this.operation = feed.getOperation();
    this.entityId = feed.getEntityId();
  }
}
