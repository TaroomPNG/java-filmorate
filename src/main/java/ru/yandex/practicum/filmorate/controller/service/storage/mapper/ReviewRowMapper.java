package ru.yandex.practicum.filmorate.controller.service.storage.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Review;

@Component("reviewRowMapper")
public class ReviewRowMapper implements RowMapper<Review> {
  @Override
  public Review mapRow(ResultSet rs, int rowNum) throws SQLException {
    return new Review(
        rs.getLong("review_id"),
        rs.getString("content"),
        rs.getBoolean("is_positive"),
        rs.getLong("user_id"),
        rs.getLong("film_id"),
        rs.getInt("useful"));
  }
}
