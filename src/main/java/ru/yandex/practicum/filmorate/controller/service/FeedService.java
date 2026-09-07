package ru.yandex.practicum.filmorate.controller.service;

import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controller.exceptions.UserNotFound;
import ru.yandex.practicum.filmorate.controller.service.storage.FeedStorage;
import ru.yandex.practicum.filmorate.controller.service.storage.UserStorage;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.dto.feedDto.FeedPostRequest;
import ru.yandex.practicum.filmorate.model.dto.feedDto.FeedResponse;

@Slf4j
@Service
public class FeedService {

  @Autowired FeedStorage feedStorage;
  @Autowired UserStorage userStorage;

  public void addToFeed(FeedPostRequest feed) {
    log.trace("Запись действия в feed");
    feedStorage.addFeed(feed);
    log.debug("Запись - {}, успешно добавлена", feed);
  }

  public List<FeedResponse> getFeedByUser(Long userId) {
    log.trace("Запрос getFeedByUser");
    if (!userStorage.isUserExists(userId)) {
      throw new UserNotFound(userId);
    }
    return new ArrayList<>(
        feedStorage.getFeedByUser(userId).stream().map(FeedResponse::new).toList());
  }

  public List<FeedResponse> getFeedByEvent(Long userId, EventType type) {
    if (!userStorage.isUserExists(userId)) {
      throw new UserNotFound(userId);
    }

    return new ArrayList<>(
        feedStorage.getFeedByUserViaType(userId, type).stream().map(FeedResponse::new).toList());
  }
}
