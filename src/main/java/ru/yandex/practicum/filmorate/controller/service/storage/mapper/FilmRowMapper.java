package ru.yandex.practicum.filmorate.controller.service.storage.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashSet;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Rating;

@Component("filmRowMapper")
public class FilmRowMapper implements RowMapper<Film> {

  @Override
  public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
    return new Film(
        rs.getLong("film_id"),
        rs.getString("name"),
        rs.getString("description"),
        rs.getDate("release_date").toLocalDate(),
        rs.getInt("duration"),
        new LinkedHashSet<>(),
        new Rating(rs.getInt("rating_id"), rs.getString("rating")),
        new LinkedHashSet<>());
  }
}
