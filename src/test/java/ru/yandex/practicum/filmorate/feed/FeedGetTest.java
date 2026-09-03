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
    User viewer = addUser("test@yandex.ru", "TEST");
    User friend = addUser("test2@yandex.ru", "TEST2");
    Film film = addFilm("TEST");

    userStorage.addFriend(viewer.getId(), friend.getId());
    Long eventId = addFeed(friend.getId(), EventType.LIKE, Operation.ADD, film.getId());

    List<Feed> feed = feedStorage.getFeedByUser(viewer.getId());

    assertThat(feed).hasSize(1);
    assertThat(feed.get(0))
        .hasFieldOrPropertyWithValue("id", eventId)
        .hasFieldOrPropertyWithValue("userId", friend.getId())
        .hasFieldOrPropertyWithValue("eventType", EventType.LIKE)
        .hasFieldOrPropertyWithValue("operation", Operation.ADD)
        .hasFieldOrPropertyWithValue("entityId", film.getId());
  }

  @Test
  void getFeedEmptyWithoutFriends() {
    User viewer = addUser("test@yandex.ru", "TEST");
    User other = addUser("test2@yandex.ru", "TEST2");
    Film film = addFilm("TEST");

    addFeed(other.getId(), EventType.LIKE, Operation.ADD, film.getId());

    assertThat(feedStorage.getFeedByUser(viewer.getId())).isEmpty();
  }

  @Test
  void getFeedEmptyWhenFriendHasNoEvents() {
    User viewer = addUser("test@yandex.ru", "TEST");
    User friend = addUser("test2@yandex.ru", "TEST2");

    userStorage.addFriend(viewer.getId(), friend.getId());

    assertThat(feedStorage.getFeedByUser(viewer.getId())).isEmpty();
  }

  @Test
  void getFeedOnlyFromFriends() {
    User viewer = addUser("test@yandex.ru", "TEST");
    User friend = addUser("test2@yandex.ru", "TEST2");
    User stranger = addUser("test3@yandex.ru", "TEST3");
    Film film = addFilm("TEST");

    userStorage.addFriend(viewer.getId(), friend.getId());
    addFeed(friend.getId(), EventType.LIKE, Operation.ADD, film.getId());
    addFeed(stranger.getId(), EventType.LIKE, Operation.ADD, film.getId());

    assertThat(feedStorage.getFeedByUser(viewer.getId()))
        .extracting(Feed::getUserId)
        .containsExactly(friend.getId());
  }

  @Test
  void getFeedMultipleEventsOrderedByTimestampDesc() throws InterruptedException {
    User viewer = addUser("test@yandex.ru", "TEST");
    User friend = addUser("test2@yandex.ru", "TEST2");
    Film film = addFilm("TEST");
    User third = addUser("test3@yandex.ru", "TEST3");

    userStorage.addFriend(viewer.getId(), friend.getId());
    Long older = addFeed(friend.getId(), EventType.LIKE, Operation.ADD, film.getId());
    Thread.sleep(5);
    Long newer = addFeed(friend.getId(), EventType.FRIEND, Operation.ADD, third.getId());

    assertThat(feedStorage.getFeedByUser(viewer.getId()))
        .extracting(Feed::getId)
        .containsExactly(newer, older);
  }

  @Test
  void getFeedByType() {
    User viewer = addUser("test@yandex.ru", "TEST");
    User friend = addUser("test2@yandex.ru", "TEST2");
    User third = addUser("test3@yandex.ru", "TEST3");
    Film film = addFilm("TEST");

    userStorage.addFriend(viewer.getId(), friend.getId());
    addFeed(friend.getId(), EventType.LIKE, Operation.ADD, film.getId());
    addFeed(friend.getId(), EventType.FRIEND, Operation.ADD, third.getId());
    addFeed(friend.getId(), EventType.LIKE, Operation.REMOVE, film.getId());

    assertThat(feedStorage.getFeedByUserViaType(viewer.getId(), EventType.LIKE))
        .extracting(Feed::getEventType)
        .containsOnly(EventType.LIKE)
        .hasSize(2);
    assertThat(feedStorage.getFeedByUserViaType(viewer.getId(), EventType.FRIEND))
        .extracting(Feed::getEventType)
        .containsExactly(EventType.FRIEND);
    assertThat(feedStorage.getFeedByUserViaType(viewer.getId(), EventType.REVIEW)).isEmpty();
  }

  @Test
  void isFeedExistByUser() {
    User viewer = addUser("test@yandex.ru", "TEST");
    User friend = addUser("test2@yandex.ru", "TEST2");
    Film film = addFilm("TEST");

    userStorage.addFriend(viewer.getId(), friend.getId());

    assertThat(feedStorage.isFeedExistByUser(viewer.getId())).isFalse();

    addFeed(friend.getId(), EventType.LIKE, Operation.ADD, film.getId());

    assertThat(feedStorage.isFeedExistByUser(viewer.getId())).isTrue();
  }
}
