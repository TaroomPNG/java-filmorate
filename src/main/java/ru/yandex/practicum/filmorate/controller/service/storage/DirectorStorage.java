package ru.yandex.practicum.filmorate.controller.service.storage;

import java.util.List;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.dto.directorDto.DirectorPostRequest;
import ru.yandex.practicum.filmorate.model.dto.directorDto.DirectorPutRequest;

public interface DirectorStorage {
  Director addDirector(DirectorPostRequest directorPostRequest);

  Director updateDirector(DirectorPutRequest directorPutRequest);

  boolean deleteDirector(Long directorId);

  Director getDirector(Long directorId);

  List<Director> getAllDirectors();

  boolean isDirectorExist(Long directorId);
}
