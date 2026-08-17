package ru.yandex.practicum.filmorate.model;

public enum FriendStatus {
  // Для того, чтобы можно было смотреть входящие заявки - нужно добавить еще что-то из разряда
  // INCOMING
  // Но на данный момент оставлю такую логику, ибо уже 17 число, а я даж бд не начал
  // проектировать...
  UNCONFIRMED,
  CONFIRMED
}
