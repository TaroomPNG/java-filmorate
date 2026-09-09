package ru.yandex.practicum.filmorate.controller.service.storage;

import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.controller.exceptions.DirectorNotFound;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.dto.directorDto.DirectorPostRequest;
import ru.yandex.practicum.filmorate.model.dto.directorDto.DirectorPutRequest;

@Repository("DirectorDbStorage")
public class DirectorDbStorage extends BaseRepository<Director> implements DirectorStorage {

  private static final String SELECT_DIRECTOR = "SELECT d.director_id, d.name FROM director AS d ";
  private static final String FIND_BY_ID_DIRECTOR = SELECT_DIRECTOR + "WHERE director_id = ?";
  private static final String FIND_ALL_DIRECTOR = SELECT_DIRECTOR + "ORDER BY d.director_id";

  private static final String ADD_NEW_DIRECTOR = "INSERT INTO director (name) VALUES (?)";

  private static final String DELETE_FILM_TO_DIRECTORS =
      "DELETE FROM film_to_directors WHERE director_id = ?";
  private static final String DELETE_BY_ID_DIRECTOR = "DELETE FROM director WHERE director_id = ?";

  public DirectorDbStorage(JdbcTemplate jdbc, RowMapper<Director> mapper) {
    super(jdbc, mapper);
  }

  @Override
  public Director addDirector(DirectorPostRequest directorPostRequest) {

    long id = insert(ADD_NEW_DIRECTOR, directorPostRequest.getName());
    return getDirector(id);
  }

  @Override
  public Director updateDirector(DirectorPutRequest directorPutRequest) {
    if (!isDirectorExist(directorPutRequest.getId())) {
      throw new DirectorNotFound(directorPutRequest.getId());
    }

    String updateQuery = "UPDATE director SET name = ? WHERE director_id = ?";

    jdbc.update(updateQuery, directorPutRequest.getName(), directorPutRequest.getId());

    return getDirector(directorPutRequest.getId());
  }

  @Override
  @Transactional
  public boolean deleteDirector(Long directorId) {
    if (!isDirectorExist(directorId)) {
      throw new DirectorNotFound(directorId);
    }
    jdbc.update(DELETE_FILM_TO_DIRECTORS, directorId);
    return delete(DELETE_BY_ID_DIRECTOR, directorId);
  }

  @Override
  public Director getDirector(Long directorId) {
    Optional<Director> optionalDirector = findOne(FIND_BY_ID_DIRECTOR, directorId);
    if (optionalDirector.isEmpty()) {
      throw new DirectorNotFound(directorId);
    }
    return optionalDirector.get();
  }

  @Override
  public List<Director> getAllDirectors() {
    return List.copyOf(findMany(FIND_ALL_DIRECTOR));
  }

  @Override
  public boolean isDirectorExist(Long directorId) {
    return findOne(FIND_BY_ID_DIRECTOR, directorId).isPresent();
  }

  public void clear() {
    jdbc.update("DELETE FROM film_to_directors");
    jdbc.update("DELETE FROM director");
    jdbc.update("ALTER TABLE director ALTER COLUMN director_id RESTART WITH 1");
  }
}
