package ru.yandex.practicum.filmorate.directors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import ru.yandex.practicum.filmorate.DaoTest;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.dto.directorDto.DirectorPostRequest;

class DirectorPostTest extends DaoTest {

  @Test
  void addCorrectDirector() {
    Director director = addDirector("TESTDirector");

    assertThat(director)
        .hasFieldOrPropertyWithValue("id", 1L)
        .hasFieldOrPropertyWithValue("name", "TESTDirector");
  }

  @Test
  void add2CorrectDirectors() {
    Director first = addDirector("TESTDirector");
    Director second = addDirector("TESTDirector2");

    assertThat(first.getId()).isEqualTo(1L);
    assertThat(second.getId()).isEqualTo(2L);
  }

  @Test
  void add2DirectorsWithSameName() {
    Director first = addDirector("TESTDirector");
    Director second = addDirector("TESTDirector");

    assertThat(first.getId()).isEqualTo(1L);
    assertThat(second.getId()).isEqualTo(2L);
    assertThat(first.getName()).isEqualTo(second.getName());
  }

  // Тест некорректных запросов
  @Test
  void addUncorrectDirector() {
    DirectorPostRequest request = new DirectorPostRequest();
    request.setName(null);

    assertThatThrownBy(() -> directorStorage.addDirector(request))
        .isInstanceOf(DataIntegrityViolationException.class);
  }
}
