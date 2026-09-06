package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.controller.service.DirectorService;
import ru.yandex.practicum.filmorate.model.dto.directorDto.DirectorPostRequest;
import ru.yandex.practicum.filmorate.model.dto.directorDto.DirectorPutRequest;
import ru.yandex.practicum.filmorate.model.dto.directorDto.DirectorResponse;

@RestController
@RequestMapping("/directors")
@RequiredArgsConstructor
public class DirectorController {

  private final DirectorService directorService;

  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  public Collection<DirectorResponse> getAllDirectors() {
    return directorService.getDirectors();
  }

  @GetMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public DirectorResponse getDirectorById(@PathVariable Long id) {
    return directorService.getDirectorById(id);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public DirectorResponse addDirector(@Valid @RequestBody DirectorPostRequest directorPostRequest) {
    return directorService.addDirector(directorPostRequest);
  }

  @PutMapping
  @ResponseStatus(HttpStatus.OK)
  public DirectorResponse updateDirector(
      @Valid @RequestBody DirectorPutRequest directorPutRequest) {
    return directorService.updateDirector(directorPutRequest);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public boolean deleteDirector(@PathVariable Long id) {
    return directorService.deleteDirector(id);
  }
}
