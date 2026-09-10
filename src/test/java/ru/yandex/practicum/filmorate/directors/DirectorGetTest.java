package ru.yandex.practicum.filmorate.directors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.DaoTest;
import ru.yandex.practicum.filmorate.controller.exceptions.DirectorNotFound;
import ru.yandex.practicum.filmorate.model.Director;

class DirectorGetTest extends DaoTest {

  @Test
  void getOneDirector() {
    Director created = addDirector("TESTDirector");

    List<Director> directors = directorStorage.getAllDirectors();

    assertThat(directors).hasSize(1);
    assertThat(directors.getFirst())
        .hasFieldOrPropertyWithValue("id", created.getId())
        .hasFieldOrPropertyWithValue("name", "TESTDirector");
  }

  @Test
  void getManyDirectors() {
    addDirector("TESTDirector");
    addDirector("TESTDirector2");
    addDirector("TESTDirector3");

    List<Director> directors = directorStorage.getAllDirectors();

    assertThat(directors).extracting(Director::getId).containsExactly(1L, 2L, 3L);
  }

  @Test
  void getDirectorById() {
    Director created = addDirector("TESTDirector");

    Director director = directorStorage.getDirector(created.getId());

    assertThat(director)
        .hasFieldOrPropertyWithValue("id", created.getId())
        .hasFieldOrPropertyWithValue("name", "TESTDirector");
  }

  @Test
  void getDirectorByIdNotFound() {
    assertThatThrownBy(() -> directorStorage.getDirector(9999L))
        .isInstanceOf(DirectorNotFound.class);
  }

  @Test
  void isDirectorExist() {
    Director created = addDirector("TESTDirector");

    assertThat(directorStorage.isDirectorExist(created.getId())).isTrue();
    assertThat(directorStorage.isDirectorExist(9999L)).isFalse();
  }
}
