package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
  public ResponseEntity<Collection<DirectorResponse>> getAllDirectors() {
    return ResponseEntity.ok(directorService.getDirectors());
  }

  @GetMapping("/{id}")
  public ResponseEntity<DirectorResponse> getDirectorById(@PathVariable Long id) {
    return ResponseEntity.ok(directorService.getDirectorById(id));
  }

  @PostMapping
  public ResponseEntity<DirectorResponse> addDirector(@Valid @RequestBody DirectorPostRequest directorPostRequest) {
    return ResponseEntity.status(HttpStatus.CREATED).body(directorService.addDirector(directorPostRequest));
  }

  @PutMapping
  public ResponseEntity<DirectorResponse> updateDirector(
      @Valid @RequestBody DirectorPutRequest directorPutRequest) {
    return ResponseEntity.ok(directorService.updateDirector(directorPutRequest));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Boolean> deleteDirector(@PathVariable Long id) {
    return ResponseEntity.status(HttpStatus.NO_CONTENT).body(directorService.deleteDirector(id));
  }
}
