package ru.yandex.practicum.filmorate.reviews;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.DaoTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;

@DisplayName("Тесты лайков и дизлайков отзывов")
class ReviewLikeTest extends DaoTest {

  @Test
  @DisplayName("Лайк должен увеличивать полезность отзыва на 1")
  void shouldAddLikeIncreasesUsefulByOne() {
    User author = addUser("authorlike@test.ru", "authorLike");
    User voter = addUser("voterlike@test.ru", "voterLike");
    Film film = addFilm("likeFilm");
    Review created = addReview("review", true, author.getId(), film.getId());

    reviewStorage.addLike(created.getId(), voter.getId());

    assertThat(reviewStorage.getReviewById(created.getId()).getUseful()).isEqualTo(1);
  }

  @Test
  @DisplayName("Дизлайк должен уменьшать полезность отзыва на 1")
  void shouldAddDislikeDecreasesUsefulByOne() {
    User author = addUser("authordislike@test.ru", "authorDislike");
    User voter = addUser("voterdislike@test.ru", "voterDislike");
    Film film = addFilm("dislikeFilm");
    Review created = addReview("review", true, author.getId(), film.getId());

    reviewStorage.addDislike(created.getId(), voter.getId());

    assertThat(reviewStorage.getReviewById(created.getId()).getUseful()).isEqualTo(-1);
  }

  @Test
  @DisplayName("Смена лайка на дизлайк должна корректно пересчитывать полезность")
  void shouldSwitchLikeToDislikeRecalculatesUseful() {
    User author = addUser("switch@author.ru", "switchAuthor");
    User voter = addUser("switch@voter.ru", "switchVoter");
    Film film = addFilm("switchFilm");
    Review created = addReview("review", true, author.getId(), film.getId());

    reviewStorage.addLike(created.getId(), voter.getId());
    reviewStorage.addDislike(created.getId(), voter.getId());

    assertThat(reviewStorage.getReviewById(created.getId()).getUseful()).isEqualTo(-1);
  }

  @Test
  @DisplayName("Удаление лайка и дизлайка должно возвращать полезность к нулю")
  void shouldRemoveLikeAndDislikeBackToZero() {
    User author = addUser("remove@author.ru", "removeAuthor");
    User voter = addUser("remove@voter.ru", "removeVoter");
    Film film = addFilm("removeFilm");
    Review likeReview = addReview("like", true, author.getId(), film.getId());
    Review dislikeReview = addReview("dislike", true, author.getId(), film.getId());

    reviewStorage.addLike(likeReview.getId(), voter.getId());
    reviewStorage.addDislike(dislikeReview.getId(), voter.getId());
    reviewStorage.removeLike(likeReview.getId(), voter.getId());
    reviewStorage.removeDislike(dislikeReview.getId(), voter.getId());

    assertThat(reviewStorage.getReviewById(likeReview.getId()).getUseful()).isEqualTo(0);
    assertThat(reviewStorage.getReviewById(dislikeReview.getId()).getUseful()).isEqualTo(0);
  }
}
