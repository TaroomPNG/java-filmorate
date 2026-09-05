package ru.yandex.practicum.filmorate.feed;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.DaoTest;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.model.User;

class FeedGetTest extends DaoTest {

  @Test
  void getCorrectFeed() {
    User user = addUser("test@yandex.ru", "TEST");
    Film film = addFilm("TEST");

    Long eventId = addFeed(user.getId(), EventType.LIKE, Operation.ADD, film.getId());

    List<Feed> feed = feedStorage.getFeedByUser(user.getId());

    assertThat(feed).hasSize(1);
    assertThat(feed.get(0))
        .hasFieldOrPropertyWithValue("id", eventId)
        .hasFieldOrPropertyWithValue("userId", user.getId())
        .hasFieldOrPropertyWithValue("eventType", EventType.LIKE)
        .hasFieldOrPropertyWithValue("operation", Operation.ADD)
        .hasFieldOrPropertyWithValue("entityId", film.getId());
  }

  @Test
  void getFeedEmptyWhenUserHasNoEvents() {
    User user = addUser("test@yandex.ru", "TEST");
    User other = addUser("test2@yandex.ru", "TEST2");
    Film film = addFilm("TEST");

    addFeed(other.getId(), EventType.LIKE, Operation.ADD, film.getId());

    assertThat(feedStorage.getFeedByUser(user.getId())).isEmpty();
  }

  @Test
  void getFeedOnlyOwnEvents() {
    User user = addUser("test@yandex.ru", "TEST");
    User other = addUser("test2@yandex.ru", "TEST2");
    Film film = addFilm("TEST");

    addFeed(user.getId(), EventType.LIKE, Operation.ADD, film.getId());
    addFeed(other.getId(), EventType.LIKE, Operation.ADD, film.getId());

    assertThat(feedStorage.getFeedByUser(user.getId()))
        .extracting(Feed::getUserId)
        .containsExactly(user.getId());
  }

  @Test
  void getFeedMultipleEventsOrderedByTimestampAsc() throws InterruptedException {
    User user = addUser("test@yandex.ru", "TEST");
    Film film = addFilm("TEST");
    User friend = addUser("test2@yandex.ru", "TEST2");

    Long older = addFeed(user.getId(), EventType.LIKE, Operation.ADD, film.getId());
    Thread.sleep(5);
    Long newer = addFeed(user.getId(), EventType.FRIEND, Operation.ADD, friend.getId());

    assertThat(feedStorage.getFeedByUser(user.getId()))
        .extracting(Feed::getId)
        .containsExactly(older, newer);
  }

  @Test
  void getFeedByType() {
    User user = addUser("test@yandex.ru", "TEST");
    User friend = addUser("test2@yandex.ru", "TEST2");
    Film film = addFilm("TEST");

    addFeed(user.getId(), EventType.LIKE, Operation.ADD, film.getId());
    addFeed(user.getId(), EventType.FRIEND, Operation.ADD, friend.getId());
    addFeed(user.getId(), EventType.LIKE, Operation.REMOVE, film.getId());

    assertThat(feedStorage.getFeedByUserViaType(user.getId(), EventType.LIKE))
        .extracting(Feed::getEventType)
        .containsOnly(EventType.LIKE)
        .hasSize(2);
    assertThat(feedStorage.getFeedByUserViaType(user.getId(), EventType.FRIEND))
        .extracting(Feed::getEventType)
        .containsExactly(EventType.FRIEND);
    assertThat(feedStorage.getFeedByUserViaType(user.getId(), EventType.REVIEW)).isEmpty();
  }

  @Test
  void isFeedExistByUser() {
    User user = addUser("test@yandex.ru", "TEST");
    Film film = addFilm("TEST");

    assertThat(feedStorage.isFeedExistByUser(user.getId())).isFalse();

    addFeed(user.getId(), EventType.LIKE, Operation.ADD, film.getId());

    assertThat(feedStorage.isFeedExistByUser(user.getId())).isTrue();
  }
}
