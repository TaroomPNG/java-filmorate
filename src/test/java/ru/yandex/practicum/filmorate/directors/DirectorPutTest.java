package ru.yandex.practicum.filmorate.directors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.DaoTest;
import ru.yandex.practicum.filmorate.controller.exceptions.DirectorNotFound;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.dto.directorDto.DirectorPutRequest;

class DirectorPutTest extends DaoTest {

  @Test
  void putCorrectDirector() {
    Director created = addDirector("TESTDirector");

    DirectorPutRequest request = new DirectorPutRequest();
    request.setId(created.getId());
    request.setName("TESTDirector2");

    Director updated = directorStorage.updateDirector(request);

    assertThat(updated)
        .hasFieldOrPropertyWithValue("id", created.getId())
        .hasFieldOrPropertyWithValue("name", "TESTDirector2");
  }

  @Test
  void putIncorrectDirectorId() {
    DirectorPutRequest request = new DirectorPutRequest();
    request.setId(9999L);
    request.setName("TESTDirector2");

    assertThatThrownBy(() -> directorStorage.updateDirector(request))
        .isInstanceOf(DirectorNotFound.class);
  }
}
