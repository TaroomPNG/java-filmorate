package ru.yandex.practicum.filmorate.controller.service.storage.mapper;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

@Component("userRowMapper")
public class UserRowMapper implements RowMapper<User> {

  @Override
  public User mapRow(ResultSet rs, int rowNum) throws SQLException {
    Date birthday = rs.getDate("birthday");
    return new User(
        rs.getLong("user_id"),
        rs.getString("email"),
        rs.getString("login"),
        birthday != null ? birthday.toLocalDate() : null,
        rs.getString("name"));
  }
}
