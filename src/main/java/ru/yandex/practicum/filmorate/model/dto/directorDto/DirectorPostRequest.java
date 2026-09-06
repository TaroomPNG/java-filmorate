package ru.yandex.practicum.filmorate.model.dto.directorDto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DirectorPostRequest {

  @NotBlank(message = "Имя режиссера обязательно")
  private String name;
}
