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

  private static final String FEED_SELECT =
      "SELECT fd.event_id, fd.timestamp, fd.user_id, fd.event_type, fd.operation, fd.entity_id "
          + "FROM feed AS fd "
          + "JOIN user_friendship AS uf ON fd.user_id = uf.friend_id "
          + "JOIN friendship_status AS fs ON uf.status_id = fs.id ";
  private static final String CONFIRMED_FRIENDSHIP = "fs.status = 'CONFIRMED' AND uf.user_id = ?";
  private static final String FIND_BY_ID_USER =
      FEED_SELECT + "WHERE " + CONFIRMED_FRIENDSHIP + " ORDER BY fd.timestamp DESC";

  private static final String FIND_BY_ID_USER_AND_TYPE =
      FEED_SELECT
          + "WHERE "
          + CONFIRMED_FRIENDSHIP
          + " AND fd.event_type = ? ORDER BY fd.timestamp DESC";

  private static final String EXISTS_BY_USER =
      "SELECT COUNT(*) FROM feed AS fd "
          + "JOIN user_friendship AS uf ON fd.user_id = uf.friend_id "
          + "JOIN friendship_status AS fs ON uf.status_id = fs.id "
          + "WHERE "
          + CONFIRMED_FRIENDSHIP;

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
    Integer count = jdbc.queryForObject(EXISTS_BY_USER, Integer.class, id);
    return count != null && count > 0;
  }

  @Override
  public void clear() {
    jdbc.update("DELETE FROM feed");
    jdbc.update("ALTER TABLE feed ALTER COLUMN event_id RESTART WITH 1");
  }
}
