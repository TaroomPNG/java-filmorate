package ru.yandex.practicum.filmorate.controller.service.storage.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Operation;

@Component("feedRowMapper")
public class FeedRowMapper implements RowMapper<Feed> {
  @Override
  public Feed mapRow(ResultSet rs, int rowNum) throws SQLException {
    return new Feed(
        rs.getLong("event_id"),
        rs.getLong("timestamp"),
        rs.getLong("user_id"),
        EventType.valueOf(rs.getString("event_type")),
        Operation.valueOf(rs.getString("operation")),
        rs.getLong("entity_id"));
  }
}
