package ru.yandex.practicum.filmorate.controller.service.storage.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;

@Component("directorRowMapper")
public class DirectorRowMapper implements RowMapper<Director> {

  @Override
  public Director mapRow(ResultSet rs, int rowNum) throws SQLException {
    return new Director(rs.getLong("director_id"), rs.getString("name"));
  }
}
