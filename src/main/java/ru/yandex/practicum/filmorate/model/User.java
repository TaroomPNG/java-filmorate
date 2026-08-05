package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = {"email"})
@AllArgsConstructor
public class User {
  private Long id;
  private String email;
  private String login;
  private LocalDate birthday;
  private String name;
}
