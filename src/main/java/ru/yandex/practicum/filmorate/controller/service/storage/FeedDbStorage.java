package ru.yandex.practicum.filmorate.controller.service.storage;

import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.dto.feedDto.FeedPostRequest;

@Repository("FeedDbStorage")
public class FeedDbStorage extends BaseRepository<Feed> implements FeedStorage {

  private static final String ADD_NEW_FEED =
      "INSERT INTO feed (timestamp, user_id, event_type, operation, entity_id) "
          + "VALUES (? , ? , ? , ? , ?)";

  private static final String FIND_BY_ID_USER =
      "SELECT event_id, timestamp, user_id, event_type, operation, entity_id "
          + "FROM feed WHERE user_id = ? ORDER BY timestamp ASC";

  private static final String FIND_BY_ID_USER_AND_TYPE =
      "SELECT event_id, timestamp, user_id, event_type, operation, entity_id "
          + "FROM feed WHERE user_id = ? AND event_type = ? ORDER BY timestamp ASC";

  private static final String EXISTS_BY_USER = "SELECT user_id FROM feed WHERE user_id = ? LIMIT 1";

  public FeedDbStorage(JdbcTemplate jdbc, RowMapper<Feed> mapper) {
    super(jdbc, mapper);
  }

  @Override
  public Long addFeed(FeedPostRequest feed) {
    return insert(
        ADD_NEW_FEED,
        feed.getTimestamp(),
        feed.getUserId(),
        feed.getEventType().name(),
        feed.getOperation().name(),
        feed.getEntityId());
  }

  @Override
  public List<Feed> getFeedByUser(Long userId) {
    return List.copyOf(findMany(FIND_BY_ID_USER, userId));
  }

  @Override
  public List<Feed> getFeedByUserViaType(Long userId, EventType type) {
    return List.copyOf(findMany(FIND_BY_ID_USER_AND_TYPE, userId, type.name()));
  }

  @Override
  public boolean isFeedExistByUser(Long id) {
    List<Long> result = jdbc.queryForList(EXISTS_BY_USER, Long.class, id);
    return !result.isEmpty();
  }

  @Override
  public void clear() {
    jdbc.update("DELETE FROM feed");
    jdbc.update("ALTER TABLE feed ALTER COLUMN event_id RESTART WITH 1");
  }
}
