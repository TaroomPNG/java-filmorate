package ru.yandex.practicum.filmorate.controller.service;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controller.service.storage.DirectorStorage;
import ru.yandex.practicum.filmorate.model.dto.directorDto.DirectorPostRequest;
import ru.yandex.practicum.filmorate.model.dto.directorDto.DirectorPutRequest;
import ru.yandex.practicum.filmorate.model.dto.directorDto.DirectorResponse;

@Service
@Slf4j
public class DirectorService {
  @Autowired
  @Qualifier("DirectorDbStorage")
  private DirectorStorage directorStorage;

  public List<DirectorResponse> getDirectors() {
    log.trace("Запрос DirectorResponse через getDirectors");
    return directorStorage.getAllDirectors().stream().map(DirectorResponse::new).toList();
  }

  public DirectorResponse getDirectorById(Long id) {
    log.trace("Запрос DirectorResponse через getDirectorById");
    return new DirectorResponse(directorStorage.getDirector(id));
  }

  public DirectorResponse addDirector(DirectorPostRequest directorPostRequest) {
    log.trace("Запрос DirectorResponse через addDirector");
    return new DirectorResponse(directorStorage.addDirector(directorPostRequest));
  }

  public DirectorResponse updateDirector(DirectorPutRequest directorPutRequest) {
    log.trace("Запрос DirectorResponse через updateDirector");
    return new DirectorResponse(directorStorage.updateDirector(directorPutRequest));
  }

  public boolean deleteDirector(Long directorId) {
    log.trace("Запрос boolean через deleteDirector");
    return directorStorage.deleteDirector(directorId);
  }
}
