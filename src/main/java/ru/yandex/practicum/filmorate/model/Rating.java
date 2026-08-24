package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Rating {
  public static final Rating G = new Rating(1, "G");
  public static final Rating PG = new Rating(2, "PG");
  public static final Rating PG_13 = new Rating(3, "PG-13");
  public static final Rating R = new Rating(4, "R");
  public static final Rating NC_17 = new Rating(5, "NC-17");

  private Integer id;
  private String name;
}
