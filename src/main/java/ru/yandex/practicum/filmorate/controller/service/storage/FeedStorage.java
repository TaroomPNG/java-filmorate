package ru.yandex.practicum.filmorate.controller.service.storage;

import java.util.List;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.dto.feedDto.FeedPostRequest;

public interface FeedStorage {
  Long addFeed(FeedPostRequest feed);

  List<Feed> getFeedByUser(Long userId);

  List<Feed> getFeedByUserViaType(Long userId, EventType type);

  boolean isFeedExistByUser(Long id);

  void clear();
}
